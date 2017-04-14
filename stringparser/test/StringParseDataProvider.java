package stringparser.test;

import java.util.ArrayList;
import java.util.List;

import mujava.plugin.DataProvider;

public class StringParseDataProvider implements DataProvider{

	@Override
	public List<Object> provideData() {
		String data0 = "ABCDEFGH8CH12345678&*^#";
		String data1 = "abcdefgh8ch12345678;;";
		String data2 = "abcdEFGH8cH12398760..";
		String data3 = "aBcDeFghIjKlMNO)*@#$12";
		String data4 = "afasdfuihHUSID123DHUdas#%@^";
		String data5 = "jiHUSDsdw75230@#$";
		List<Object> dataset = new ArrayList<Object>();
		
		dataset.add(data0);
		dataset.add(data1);
		dataset.add(data2);
		dataset.add(data3);
		dataset.add(data4);
		dataset.add(data5);
		
		return dataset;
	}

}
