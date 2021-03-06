/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.org/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.editItem.bean.ContentAbstractCollection.ContentAbstractManager;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Bean to handle the ContentSubjectCollection on a single jsp.
 * A ContentAbstractCollection is represented by a List&lt;TextVO>.
 * 
 * @author Tobias Schraut
 */
public class ContentSubjectCollection
{
    private List<TextVO> parentVO;
    private ContentSubjectManager contentSubjectManager;
    
    /**
     * Default constructor.
     */
    public ContentSubjectCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<TextVO>());
    }

    /**
     * Constructor using initial list.
     * 
     * @param parentVO The initial list of text vos.
     */
    public ContentSubjectCollection(List<TextVO> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<TextVO> getParentVO()
    {
        return parentVO;
    }

    /**
     * Sets the list of {@link TextVO}s and initializes the {@link ContentAbstractManager}.
     * 
     * @param parentVO The list of text vos.
     */
    public void setParentVO(List<TextVO> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        contentSubjectManager = new ContentSubjectManager(parentVO);
    }
    
    public SelectItem[] getLanguageOptions()
    {
        return CommonUtils.getLanguageOptions();
    }

    /**
     * Specialized DataModelManager to deal with objects of type TextVO.
     * 
     * @author Mario Wagner
     */
    public class ContentSubjectManager extends DataModelManager<TextVO>
    {
        protected List<TextVO> parentVO;
        
        /**
         * Constructor using an initial list.
         * 
         * @param parentVO The initial list.
         */
        public ContentSubjectManager(List<TextVO> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public TextVO createNewObject()
        {
            TextVO newTitle = new TextVO();
            return newTitle;
        }
        
        public List<TextVO> getDataListFromVO()
        {
            if (parentVO == null)
            {
                return null;
            }
            return parentVO;
        }

        public void setParentVO(List<TextVO> parentVO)
        {
            this.parentVO = parentVO;
            setObjectList(parentVO);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public ContentSubjectManager getContentSubjectManager()
    {
        return contentSubjectManager;
    }

    public void setContentAbstractManager(ContentSubjectManager contentAbstractManager)
    {
        this.contentSubjectManager = contentAbstractManager;
    }

}
