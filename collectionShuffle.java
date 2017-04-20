
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class tryShuffle {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String[] relCorMutsArr = {"AORB_8","AORB_9","AORB_10","AORB_11"};
		List<String> relCorMutsList = new ArrayList<String>();
		relCorMutsList.add("AORB_8");
		relCorMutsList.add("AORB_9");
		relCorMutsList.add("AORB_10");
		relCorMutsList.add("AORB_11");
		System.out.println("original list:");
		for(int i=0;i<relCorMutsList.size();i++){
			System.out.println(relCorMutsList.get(i).toString());
		}
		
		System.out.println("shuffle:");
		Collections.shuffle(relCorMutsList);
		
		
		for(int i=0;i<relCorMutsList.size();i++){
			System.out.println(relCorMutsList.get(i).toString());
		}
		
	}

}
