package de.mpg.escidoc.main;

import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.commons.io.FileUtils;

import de.mpg.escidoc.util.AllPidsCheckStatistic;
import de.mpg.escidoc.util.Statistic;

public class AllPidsCheckManager extends AbstractConsistencyCheckManager implements IConsistencyCheckManager{

	private AllPidsCheckStatistic statistic;

    private static String scanClauses[] = {
    	"escidoc.property.latest-release.pid=\"xxx\"",
    	"escidoc.property.pid=\"xxx\"", 
    	"escidoc.property.version.pid=\"xxx\"", 
    	"escidoc.compnent.pid=\"xxx\""
    };
    
    public AllPidsCheckManager()
    {
    	super.init();
        statistic = new AllPidsCheckStatistic();
    }
    
	@Override
	public void createOrCorrectSet(Set<String> objects) throws Exception 
	{
		objects = this.searchForPids();
        
        statistic = new AllPidsCheckStatistic();
        statistic.setObjectsTotal(objects.size());
       
        FileUtils.writeLines(new File("./allPids.txt"), objects);      
		
	}

	@Override
	protected void doResolve(String object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Statistic getStatistic() 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private Set<String> searchForPids() throws Exception
	{
        Set<String> pids = new HashSet<String>();
        String lastTerm = "", veryLastTerm = "";
        
		for (String scanClause : scanClauses)
		{

			ScanRequestType scanRequest = new ScanRequestType();

			scanRequest.setVersion("1.1");
			scanRequest.setResponsePosition(new NonNegativeInteger("0"));
			scanRequest.setMaximumTerms(new PositiveInteger("10000"));
			
			lastTerm = ""; veryLastTerm = "";

			do
			{
				lastTerm = veryLastTerm;
				scanRequest.setScanClause(scanClause.replace("xxx", lastTerm));

				ScanResponseType scanResponse = searchHandler.scanOperation(scanRequest);
	
				if (scanResponse.getDiagnostics() != null)
				{
					// something went wrong
					for (DiagnosticType diagnostic : scanResponse
							.getDiagnostics().getDiagnostic())
					{
						logger.info("diagnostic <" + diagnostic.getDetails() + ">");		
					}

					return pids;
				}

				if (scanResponse.getTerms() != null)
				{
					TermType[] term = scanResponse.getTerms().getTerm();

					if (term == null)
						break;
					int i;
					String value = "";
					for (i = 0; i < term.length; i++)
					{
						value = term[i].getValue().toString();
						NonNegativeInteger number = term[i].getNumberOfRecords();
								
						if (!number.equals(new NonNegativeInteger("1")))
						{
							logger.warn("Handle <" + value + "> occurs <"
									+ number.toString() + "> times!");
						}

						pids.add(value);

					}
					logger.info("found <" + i + "> handles for <" + scanClause);		
					veryLastTerm = value;
				}
			} while (!lastTerm.equals(veryLastTerm));

		}

       return pids;
	}
}
