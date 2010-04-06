/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.cone.ModelList.Model;
import de.mpg.escidoc.services.cone.ModelList.Predicate;
import de.mpg.escidoc.services.cone.util.Describable;
import de.mpg.escidoc.services.cone.util.LocalizedString;
import de.mpg.escidoc.services.cone.util.LocalizedTripleObject;
import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.ModelHelper;
import de.mpg.escidoc.services.cone.util.TreeFragment;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * SQL implementation for the {@link Querier} interface. Currently works with Postgres, but should also work with other
 * relational databases like HSQL, MySQL. For Oracle and SQL Server, maybe some modifications will be needed.
 * 
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 2609 $ $LastChangedDate: 2009-12-23 10:47:02 +0100 (Mi, 23 Dez 2009) $
 */
public class SQLQuerier implements Querier
{
    private static final String ESCIDOC_CONE_LANGUAGE_DEFAULT = "escidoc.cone.language.default";
    private static final Logger logger = Logger.getLogger(SQLQuerier.class);
    private DataSource dataSource = null;
    private Connection connection;
    protected boolean loggedIn;

    /**
     * Default constructor initializing the {@link DataSource}.
     * @throws Exception Any exception.
     */
    public SQLQuerier() throws Exception
    {
        //InitialContext context = new InitialContext();
        //dataSource = (DataSource) context.lookup("Cone");
        
        Class.forName(PropertyReader.getProperty("escidoc.cone.database.driver.class"));
        connection = DriverManager.getConnection("jdbc:postgresql://" +
                PropertyReader.getProperty("escidoc.cone.database.server.name") +
                ":" +
                PropertyReader.getProperty("escidoc.cone.database.server.port") +
                "/" +
                PropertyReader.getProperty("escidoc.cone.database.name"),
                PropertyReader.getProperty("escidoc.cone.database.user.name"),
                PropertyReader.getProperty("escidoc.cone.database.user.password"));
        
        //connection = dataSource.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Describable> query(String model, String query, ModeType modeType) throws Exception
    {
        return query(model, query, null, modeType);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Describable> query(String model, String query, String language, ModeType modeType) throws Exception
    {
        String limitString = PropertyReader.getProperty("escidoc.cone.maximum.results");
        return query(model, query, language, modeType, Integer.parseInt(limitString));
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Describable> query(String model, Pair[] searchFields, String language, ModeType modeType) throws Exception
    {
        String limitString = PropertyReader.getProperty("escidoc.cone.maximum.results");
        return query(model, searchFields, language, modeType, Integer.parseInt(limitString));
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Describable> query(String model, String searchString, String language, ModeType modeType, int limit) throws Exception
    {
        if (modeType == ModeType.FAST)
        {
            return queryFast(model, searchString, language, limit);
        }
        else if (modeType == ModeType.FULL)
        {
            return queryFull(model, searchString, language, limit);
        }
        else
        {
            throw new RuntimeException("Mode " + modeType + " not supported.");
        }
        
    }
    
    public List<? extends Describable> queryFast(String model, String searchString, String language, int limit) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }
        
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }

        language = language.replace("'", "''");
        
        String[] searchStrings = formatSearchString(searchString);
        String subQuery = "select id from matches where model = '" + model + "'";
        for (int i = 0; i < searchStrings.length; i++)
        {
            subQuery += " and";
            if (searchStrings[i].startsWith("\"") && searchStrings[i].endsWith("\""))
            {
                subQuery += " ('|' || value || '|') ilike '%|" + searchStrings[i].substring(1, searchStrings[i].length() - 1) + "|%'";
            }
            else
            {
                subQuery += " value ilike '%" + searchStrings[i] + "%'";
            }
        }
        String query = "select distinct r1.id, r1.value, r1.lang"
            + " from results r1 where id in (" + subQuery;
        query += ") and (lang = '" + language + "' or (lang is null and '" + language +
            "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
        query += " order by value, id";
        if (limit > 0)
        {
            query += " limit " + limit;
        }
        
        query += ";";
        
        logger.debug("query: " + query);
        
        Statement statement = connection.createStatement();
        long now = new Date().getTime();
        ResultSet result = statement.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        List<Pair> resultSet = new ArrayList<Pair>();
        while (result.next())
        {
            String id = result.getString("id");
            String value = result.getString("value");
            Pair pair = new Pair(id, value);
            resultSet.add(pair);
        }
        
        result.close();
        statement.close();

        return resultSet;
    }
    
    public List<? extends Describable> queryFull(String model, String searchString, String language, int limit) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }
        
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }

        language = language.replace("'", "''");
        
        String[] searchStrings = formatSearchString(searchString);
        String subQuery = "select id from matches where model = '" + model + "'";
        for (int i = 0; i < searchStrings.length; i++)
        {
            subQuery += " and";
            if (searchStrings[i].startsWith("\"") && searchStrings[i].endsWith("\""))
            {
                subQuery += " ('|' || value || '|') ilike '%|" + searchStrings[i].substring(1, searchStrings[i].length() - 1) + "|%'";
            }
            else
            {
                subQuery += " value ilike '%" + searchStrings[i] + "%'";
            }
        }
        String query = "select distinct r1.id, r1.value, r1.lang"
            + " from results r1 where id in (" + subQuery;
        query += ") and (lang = '" + language + "' or (lang is null and '" + language +
            "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
        query += " order by value, id";
        
        if (limit > 0)
        {
            query += " limit " + limit;
        }
        
        query += ";";
        
        logger.debug("query: " + query);
        
        Statement statement = connection.createStatement();
        long now = new Date().getTime();
        ResultSet result = statement.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        List<TreeFragment> resultSet = new ArrayList<TreeFragment>();
        while (result.next())
        {
            String id = result.getString("id");
            TreeFragment treeFragment = details(model, id, language);
            resultSet.add(treeFragment);
        }
        
        result.close();
        statement.close();

        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Describable> query(String modelName, Pair[] searchPairs, String language, ModeType modeType, int limit) throws Exception
    {
        if (modeType == ModeType.FAST)
        {
            return queryFast(modelName, searchPairs, language, limit);
        }
        else if (modeType == ModeType.FULL)
        {
            return queryFull(modelName, searchPairs, language, limit);
        }
        else
        {
            throw new RuntimeException("Mode " + modeType + " not supported.");
        }
    }
    
    public List<? extends Describable> queryFast(String modelName, Pair[] searchPairs, String language, int limit) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }
        
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }

        language = language.replace("'", "''");
        
        ArrayList<Pair> allPairs = new ArrayList<Pair>();
        for (Pair pair : searchPairs)
        {
            String[] predicatePieces = pair.getKey().split(":");
            if (predicatePieces.length != 2)
            {
                // field has no prefix, so ignore it.
                continue;
            }
            else
            {
                for (String key : ModelList.getInstance().getDefaultNamepaces().keySet())
                {
                    if (ModelList.getInstance().getDefaultNamepaces().get(key).equals(predicatePieces[0]))
                    {
                        // Replace prefix with uri.
                        pair.setKey(key + predicatePieces[1]);
                        break;
                    }
                }
            }
            String[] results = formatSearchString(pair.getValue());
            for (String result : results)
            {
                allPairs.add(new Pair(pair.getKey(), result));
            }
        }
        String subQuery = "select subject from triples where model = '" + modelName + "'";
        for (Pair pair : allPairs)
        {
            subQuery += " and (predicate = '" + pair.getKey() + "' and ";
            if (pair.getValue().startsWith("\"") && pair.getValue().endsWith("\""))
            {
                subQuery += " object ilike '" + pair.getValue().substring(1,pair.getValue().length() - 1) + "')";
            }
            else
            {
                subQuery += " object ilike '%" + pair.getValue() + "%')";
            }
        }
        String query = "select distinct r1.id, r1.value, r1.lang"
                + " from results r1 where id in (" + subQuery;
        query += ") and (lang = '" + language + "' or (lang is null and '" + language +
            "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
        query += " order by value, id";
        if (limit > 0)
        {
            query += " limit " + limit;
        }
        
        query += ";";
        
        logger.debug("query: " + query);
        
        Statement statement = connection.createStatement();
        long now = new Date().getTime();
        ResultSet result = statement.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        List<Pair> resultSet = new ArrayList<Pair>();
        while (result.next())
        {
            String id = result.getString("id");
            String value = result.getString("value");
            Pair pair = new Pair(id, value);
            resultSet.add(pair);
        }
        
        result.close();
        statement.close();

        return resultSet;
    }
    
    public List<? extends Describable> queryFull(String modelName, Pair[] searchPairs, String language, int limit) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }
        
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }

        language = language.replace("'", "''");
        
        ArrayList<Pair> allPairs = new ArrayList<Pair>();
        for (Pair pair : searchPairs)
        {
            String[] predicatePieces = pair.getKey().split(":");
            if (predicatePieces.length != 2)
            {
                // field has no prefix, so ignore it.
                continue;
            }
            else
            {
                for (String key : ModelList.getInstance().getDefaultNamepaces().keySet())
                {
                    if (ModelList.getInstance().getDefaultNamepaces().get(key).equals(predicatePieces[0]))
                    {
                        // Replace prefix with uri.
                        pair.setKey(key + predicatePieces[1]);
                        break;
                    }
                }
            }
            String[] results = formatSearchString(pair.getValue());
            for (String result : results)
            {
                allPairs.add(new Pair(pair.getKey(), result));
            }
        }
        String subQuery = "select subject from triples where model = '" + modelName + "'";
        for (Pair pair : allPairs)
        {
            subQuery += " and (predicate = '" + pair.getKey() + "' and ";
            if (pair.getValue().startsWith("\"") && pair.getValue().endsWith("\""))
            {
                subQuery += " object ilike '" + pair.getValue().substring(1,pair.getValue().length() - 1) + "')";
            }
            else
            {
                subQuery += " object ilike '%" + pair.getValue() + "%')";
            }
        }
        String query = "select distinct r1.id, r1.value, r1.lang"
                + " from results r1 where id in (" + subQuery;
        query += ") and (lang = '" + language + "' or (lang is null and '" + language +
            "' not in (select lang from results r2 where r2.id = r1.id and lang is not null)))";
        query += " order by value, id";
        if (limit > 0)
        {
            query += " limit " + limit;
        }
        
        query += ";";
        
        logger.debug("query: " + query);
        
        Statement statement = connection.createStatement();
        long now = new Date().getTime();
        ResultSet result = statement.executeQuery(query);
        logger.debug("Took " + (new Date().getTime() - now) + " ms.");
        List<TreeFragment> resultSet = new ArrayList<TreeFragment>();
        while (result.next())
        {
            String id = result.getString("id");
            TreeFragment treeFragment = details(modelName, id, language);
            resultSet.add(treeFragment);
        }
        
        result.close();
        statement.close();

        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    private String[] formatSearchString(String searchString)
    {
        searchString = searchString.replace("'", "''").replace('*', '%').trim();
        
        ArrayList<String> list = new ArrayList<String>();
        
        Pattern pattern = Pattern.compile("(\"[^\"]*\")");
        Matcher matcher = pattern.matcher(searchString);
        int start = 0;
        while (start < searchString.length() && matcher.find(start))
        {
            if (start < matcher.start() && !"".equals(searchString.substring(start, matcher.start()).trim()))
            {
                list.addAll(Arrays.asList(searchString.substring(start, matcher.start()).split(" ")));
            }
            list.add(matcher.group(1));
            start = matcher.end();
        }
        if (start < searchString.length())
        {
            list.addAll(Arrays.asList(searchString.substring(start).split(" ")));
        }
        
        return list.toArray(new String[]{});
    }

    /**
     * {@inheritDoc}
     */
    public TreeFragment details(String model, String id) throws Exception
    {
        return details(model, id, null);
    }

    /**
     * {@inheritDoc}
     */
    public TreeFragment details(String modelName, String id, String language) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }

        if (modelName != null)
        {
            Stack<String> idStack = new Stack<String>();
            idStack.push(id);
            
            TreeFragment result = details(modelName, id, language, idStack, connection);
            
            return result;
        }
        else
        {
            throw new NullPointerException("Model name not provided");
        }
    }

    public TreeFragment details(String modelName, String id, String language, Stack<String> idStack, Connection connection) throws Exception
    {
        if (modelName != null)
        {
            Model model = ModelList.getInstance().getModelByAlias(modelName);
            return details(modelName, model.getPredicates(), id, language, idStack, connection);
        }
        else
        {
            throw new NullPointerException("Model name not provided");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public TreeFragment details(String modelName, List<Predicate> predicates, String id, String language, Stack<String> idStack, Connection connection) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }

        id = escape(id);
        String query = "select distinct object, predicate, lang from triples where ";
        
        if (modelName != null)
        {
            query += " model = '" + modelName + "' and";
        }
        
        query += " subject = '" + id + "'";
        
        if (language == null)
        {
            language = PropertyReader.getProperty(ESCIDOC_CONE_LANGUAGE_DEFAULT);
        }
        if (language != null && !"*".equals(language))
        {
            query += " and (lang is null or lang = '" + language + "')";
        }
        logger.debug("query: " + query);
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        TreeFragment resultMap = new TreeFragment(id);
        while (result.next())
        {
            String predicateValue = result.getString("predicate");
            String object = result.getString("object");
            String lang = result.getString("lang");
            
            LocalizedTripleObject localizedTripleObject = null;

            boolean found = false;
            for (Predicate predicate : predicates)
            {
                if (predicate.getId().equals(predicateValue))
                {
                	if(!predicate.isRestricted() || loggedIn){
	                    if (predicate.isResource() && !(idStack.contains(object)))
	                    {
	                        idStack.push(object);
	                        localizedTripleObject = details(predicate.getResourceModel(), object, language, idStack, connection);
	                        idStack.pop();
	                        localizedTripleObject.setLanguage(lang);
	                    }
	                    else if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
	                    {
	                        localizedTripleObject = details(null, predicate.getPredicates(), object, language, idStack, connection);
	                        localizedTripleObject.setLanguage(lang);
	                    }
	                    else
	                    {
	                        localizedTripleObject = new LocalizedString(object, lang);
	                    }
	                    found = true;
	                    break;
                	}
                }
            }
            if (!found)
            {
                logger.error("Predicate '" + predicateValue + "' (subject = '" + id + "') not found in model '" + modelName + "'");
                //throw new RuntimeException("Predicate '" + predicateValue + "' not found in model.");
            }
            else
            {
                if (resultMap.containsKey(predicateValue))
                {
                    resultMap.get(predicateValue).add(localizedTripleObject);
                }
                else
                {
                    ArrayList<LocalizedTripleObject> newEntry = new ArrayList<LocalizedTripleObject>();
                    newEntry.add(localizedTripleObject);
                    resultMap.put(predicateValue, newEntry);
                }
            }
        }
        
        result.close();
        statement.close();
        
        return resultMap;
    }

    /**
     * Returns a SQL safe representation of the given String.
     * 
     * @param str The string that should be escaped
     * @return The escaped string
     */
    private String escape(String str)
    {
        return str.replace("'", "''");
    }

    /**
     * {@inheritDoc}
     */
    public void create(String modelName, String id, TreeFragment values) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }

        String query = "select count(subject) as cnt from triples where subject = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        
        ResultSet result = statement.executeQuery();
        if (result.next())
        {
            int count = result.getInt("cnt");
            if (count > 0)
            {
                if (modelName != null)
                {
                    throw new RuntimeException("Trying to create a resource that is already existing: " + modelName + " " + id);
                }
                else
                {
                    // Won't update an existing resource linked from this resource
                    result.close();
                    statement.close();
                    return;
                }
            }
        }
        else
        {
            throw new RuntimeException("Select count statement should always return a result, but did not.");
        }
        
        query = "insert into triples (subject, predicate, object, lang, model) values (?, ?,  ?, ?, ?)";
        
        result.close();
        statement.close();
        statement = connection.prepareStatement(query);
        
        for (String predicate : values.keySet())
        {
            statement.setString(1, id);
            statement.setString(2, predicate);
            statement.setString(5, modelName);
            
            for (LocalizedTripleObject object : values.get(predicate))
            {
                if (object instanceof LocalizedString && !"".equals(((LocalizedString) object).getValue()))
                {
                    statement.setString(3, ((LocalizedString) object).getValue());
                }
                else if (object instanceof TreeFragment)
                {
                    statement.setString(3, ((TreeFragment) object).getSubject());
                    create(null, ((TreeFragment) object).getSubject(), (TreeFragment) object);
                }
                else
                {
                    continue;
                }
                
                if (object.getLanguage() == null || "".equals(object.getLanguage()))
                {
                    statement.setString(4, null);
                }
                else
                {
                    statement.setString(4, object.getLanguage());
                }
                statement.executeUpdate();
            }
        }
        
        if (modelName != null)
        {
            query = "insert into results (id, value, lang) values (?, ?, ?)";
            statement.close();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, id);
            
            List<Pair> results = ModelHelper.buildObjectFromPattern(modelName, id, values, loggedIn);
            
            for (Pair pair : results)
            {
                if (pair.getValue() != null && !"".equals(pair.getValue()))
                {
                    statement.setString(2, pair.getValue());
                    if (pair.getKey() != null && "".equals(pair.getKey()))
                    {
                        statement.setString(3, null);
                    }
                    else
                    {
                        statement.setString(3, pair.getKey());
                    }
                    statement.executeUpdate();
                }
            }
            
            query = "insert into matches (id, value, lang, model) values (?, ?, ?, ?)";
            statement.close();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, id);
            statement.setString(4, modelName);
            
            results = ModelHelper.buildMatchStringFromModel(modelName, id, values, loggedIn);
            
            for (Pair pair : results)
            {
                if (pair.getValue() != null && !"".equals(pair.getValue()))
                {
                    statement.setString(2, pair.getValue());
                    if (pair.getKey() != null && "".equals(pair.getKey()))
                    {
                        statement.setString(3, null);
                    }
                    else
                    {
                        statement.setString(3, pair.getKey());
                    }
                    statement.executeUpdate();
                }
            }
        }
        statement.close();
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String modelName, String id) throws Exception
    {
        Model model = ModelList.getInstance().getModelByAlias(modelName);
        
        List<Predicate> predicates = model.getPredicates();
        
        delete(predicates, id);
    }
    
    public void delete(List<Predicate> predicates, String id) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }

        String query = "select distinct object from triples where subject = ? and predicate = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        for (Predicate predicate : predicates)
        {
            if (!predicate.isResource() && predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
            {
                
                statement.setString(1, id);
                statement.setString(2, predicate.getId());
                
                ResultSet result = statement.executeQuery();
                
                while (result.next())
                {
                    String subId = result.getString("object");
                    delete(predicate.getPredicates(), subId);
                }
                
                result.close();
            }
        }
        
        statement.close();
        query = "delete from triples where subject = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.executeUpdate();
        
        statement.close();
        query = "delete from results where id = ?"; 
        statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.executeUpdate();
        
        statement.close();
        query = "delete from matches where id = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.executeUpdate();
        
        statement.close();
        
    }

    /**
     * {@inheritDoc}
     */
    public synchronized String createUniqueIdentifier(String model) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }

        String query = "select value from properties where name = 'max_id'";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        
        if (result.next())
        {
            String maxIdAsString = result.getString("value");
            int maxId = Integer.parseInt(maxIdAsString) + 1;
                        
            query = "update properties set value = '" + maxId + "' where name = 'max_id'";
            statement.executeUpdate(query);
            
            String uid;
            if (model == null)
            {
                uid = "genid:" + maxId;
            }
            else
            {
                uid = model + "/resource/" + model + maxId;
            }
            
            result.close();
            query = "select * from triples where subject = '" + uid + "' limit 1";
            result = statement.executeQuery(query);

            if (result.next())
            {
                result.close();
                statement.close();
                return createUniqueIdentifier(model);
            }
            else
            {
                result.close();
                statement.close();
                return uid;
            }
        }
        else
        {
            result.close();
            statement.close();
            
            throw new Exception("'max_id not found in properties table'");
        }
    }

    public List<String> getAllIds(String modelName) throws Exception
    {
        if (connection.isClosed())
        {
            throw new RuntimeException("Connection was already closed.");
        }

        String query = "select distinct subject from triples where model = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, modelName);
        ResultSet result = statement.executeQuery();
        List<String> results = new ArrayList<String>();
        while (result.next())
        {
            results.add(result.getString("subject"));
        }
        
        result.close();
        statement.close();
        
        return results;
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        connection.close();
    }

    /**
     * {@inheritDoc}
     */
    public void release() throws Exception
    {
        connection.close();
    }
    
    public void setLoggedIn(boolean loggedIn){
    	this.loggedIn = loggedIn;
    }
    
    public boolean getLoggedIn(){
    	return this.loggedIn;
    }
    
}
