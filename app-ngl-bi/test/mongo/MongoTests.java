package mongo;

import java.util.ArrayList;

import org.junit.Test;

import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;
import fr.cea.ig.MongoDBDAO;
import utils.AbstractTests;
import static utils.RunMockHelper.*;

public class MongoTests extends AbstractTests {
	
	@Override
	public void init() {
		Run run1 = newRun("TEST1");
		run1.dispatch = Boolean.TRUE;
		
		Lane lane1 = newLane(1);
		run1.lanes = new ArrayList<Lane>();
		ReadSet readSet1 = newReadSet("rs1");		
		ReadSet readSet2 = newReadSet("rs2");
		ReadSet readSet3 = newReadSet("rs3");
		lane1.readsets = new ArrayList<ReadSet>();
		lane1.readsets.add(readSet1);
		lane1.readsets.add(readSet2);
		lane1.readsets.add(readSet3);		
		run1.lanes.add(lane1);
		
		Run run2 = newRun("TEST2");
		run2.lanes = new ArrayList<Lane>();
		run2.dispatch = Boolean.TRUE;
		
		Lane lane2 = newLane(1);
		lane2.readsets = new ArrayList<ReadSet>();
		ReadSet readSet4 = newReadSet("rs4");
		ReadSet readSet5 = newReadSet("rs5");
		ReadSet readSet6 = newReadSet("rs6");
		
		lane2.readsets.add(readSet4);
		lane2.readsets.add(readSet5);
		lane2.readsets.add(readSet6);		
		run2.lanes.add(lane2);
		
		
		Run run3 = newRun("TEST3");
		run3.lanes = new ArrayList<Lane>();
		run3.dispatch = Boolean.FALSE;
		
		Lane lane3 = newLane(1);
		lane3.readsets = new ArrayList<ReadSet>();
		ReadSet readSet7 = newReadSet("rs7");
		ReadSet readSet8 = newReadSet("rs8");
		ReadSet readSet9 = newReadSet("rs9");
		
		lane3.readsets.add(readSet7);
		lane3.readsets.add(readSet8);
		lane3.readsets.add(readSet9);		
		run3.lanes.add(lane3);
		
		MongoDBDAO.save("test.mongotests", run1);
		MongoDBDAO.save("test.mongotests", run2);
		MongoDBDAO.save("test.mongotests", run3);
		
	}
	
	@Test
	public void testSearchArchive(){
		
	}
	
	
	
}
