package stringparser.test;

import mujava.plugin.DataProvider;
import mujava.plugin.Plugin;
import mujava.plugin.SpecificationProvider;

public class StringParsePlugin {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SpecificationProvider specs = new StringParseSpecificationProvider();
		DataProvider dataSet = new StringParseDataProvider();

		Plugin stringParsePlugin = new Plugin(specs, dataSet);
		
		stringParsePlugin.execute(args);
		
	}

}
