package stringparser.test;

import mujava.plugin.SpecificationProvider;

public class StringParseSpecificationProvider implements SpecificationProvider{
	
	@Override
	public Object provideSpecification(Object args) {
		// TODO Auto-generated method stub
		
		int[] result = new int[3];
		String str = (String)args;
		
		for(int i=0;i<str.length();i++){
			if(Character.isLetter(str.charAt(i))){
				result[0]++;
			}else if(Character.isDigit(str.charAt(i))){
				result[1]++;
			}else{
				result[2]++;
			}
		}
		
		return result;
	}

	@Override
	public boolean testAgainstSpecification(Object mutantTestResult, Object specification) {
		// TODO Auto-generated method stub
		
		for (int index = 0; index < ((int[]) mutantTestResult).length; index++) {

			if (((int[]) mutantTestResult)[index] != ((int[]) specification)[index]) {
				return false;
			}

		}

		return true;
		
	}

	@Override
	public boolean testForStrictlyRelativeCorrectness(Object mutantTestResult, Object baseProgramResult,
			Object specification) {
		// TODO Auto-generated method stub
		
		int[] specificationArray = (int[]) specification;
		int[] baseProgramResultArray = (int[]) baseProgramResult;
		int[] mutantTestResultArray = (int[]) mutantTestResult;

		boolean moreCorrectMutant = false;

		for (int i = 0; i < specificationArray.length; i++) {

			if (specificationArray[i] == baseProgramResultArray[i]) {
				if (mutantTestResultArray[i] != specificationArray[i]) {
					moreCorrectMutant = false;
					return moreCorrectMutant;
				}
			} else if (mutantTestResultArray[i] == specificationArray[i]) {
				moreCorrectMutant = true;
			}
		}

		return moreCorrectMutant;
		
	}

	@Override
	public boolean testForRelativeCorrectness(Object mutantTestResult, Object baseProgramResult, Object specification) {
		// TODO Auto-generated method stub
		
		int[] specificationArray = (int[]) specification;
		int[] baseProgramResultArray = (int[]) baseProgramResult;
		int[] mutantTestResultArray = (int[]) mutantTestResult;

		boolean moreCorrectMutant = false;

		for (int i = 0; i < specificationArray.length; i++) {

			if (specificationArray[i] != baseProgramResultArray[i]
					&& specificationArray[i] == mutantTestResultArray[i]) {
				moreCorrectMutant = true;
				return moreCorrectMutant;
			}
		}
		return moreCorrectMutant;
	}
}
