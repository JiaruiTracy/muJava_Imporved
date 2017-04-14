package mujava.plugin;

import static mujava.cli.testnew.sessionName;
import static util.Constants.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import analyzer.CorrectnessAnalyzer;

import util.CustomCompiler;
import util.FileUtil;
import util.MuJavaWrapper;

/**
 * @author Shreyash
 */
public class Plugin {

	private DataProvider dataSet;
	private SpecificationProvider specs;

	private String operatorString;
	private String baseProgram;
	// private String sessionName;////replace import package
	private File combinedMutantFile;

	private Class<? extends Object> combinedMutantsClazz = null;
	private Class<? extends Object> baseProgramClazz = null;

	private CustomCompiler compiler;
	private MuJavaWrapper muJavaWrapper = null;
	private FileUtil fileUtil;
	private CorrectnessAnalyzer analyzer;

	/*
	 * use stack to save args use queue to track path, by saving session path
	 * 
	 * define a ArrayList<String> to store relative correctness mutants
	 */
	private Stack<String[]> mutantStack = new Stack<String[]>();
	// private Queue<String> mutantQueue = new LinkedList<String>();

	private List<String> usefulMutants = new ArrayList<String>();

	public Plugin(SpecificationProvider specs, DataProvider dataSet) {

		compiler = new CustomCompiler();
		fileUtil = new FileUtil();

		this.specs = specs;
		this.dataSet = dataSet;
	}

	private void parseArgs(String[] args) {

		if (args == null || args.length != 3) {
			throw new IllegalArgumentException("Please provide session name and list of mutation operators as argument."
					+ "Example -> session=session1 operator=-AORB basep=C:\\Users\\Shreyash\\Desktop\\cal.java");
		}

		for (String arg : args) {
			String[] tempArg = arg.split("=");

			switch (tempArg[0]) {
			case "session":
				sessionName = tempArg[1];
				break;
			case "operator":
				operatorString = tempArg[1];
				break;
			case "basep":
				baseProgram = tempArg[1];
				/*
				 * add path to queue
				 */
				// addToQueue(baseProgram);
				break;
			default:
				throw new IllegalArgumentException(
						"Please provide session name and list of mutation operators as argument."
								+ "Example -> session=session1 operator=-AORB basep=C:\\Users\\Shreyash"
								+ "\\Desktop\\cal.java");
			}
		}

		muJavaWrapper = new MuJavaWrapper(baseProgram, operatorString);

		System.out.println("Session Name: " + sessionName);
		System.out.println("Operator Name: " + operatorString);
		System.out.println("Base Program Name: " + baseProgram);

	}

	private void generateMutantsUsingMuJava() {

		muJavaWrapper.setMujavaEnv();
		muJavaWrapper.createTestSession();
		muJavaWrapper.generateMutants();
	}

	private void combineMutantMethodsInSingleJavaFile() {
		/*
		 * @modifier Jiarui Tian
		 * replace the static combined mutant file name with dynamic file name
		 * reason: If the name of the files which needed to be compiled are same,
		 * JVM code cache only load the first compiled file
		 */
		String combinedMutantFileName = sessionName+"CombinedMutants";
		combinedMutantFile = fileUtil.combineMutants(baseProgram, combinedMutantFileName);
	}

	@SuppressWarnings("unchecked")
	private void compileAll() {

		System.out.println("combinedMutantFile.getAbsolutePath(): " + combinedMutantFile.getAbsolutePath());

		combinedMutantsClazz = compiler.compile(combinedMutantFile.getAbsolutePath());
		baseProgramClazz = compiler.compile(baseProgram);
	}

	private void analyze() {
		// SpecificationProvider specs = new UglyNumberSpecificationProvider();
		// DataProvider dataSet = new UglyNumberDataProvider();

		analyzer = new CorrectnessAnalyzer(combinedMutantsClazz, baseProgramClazz);
		usefulMutants = analyzer.analyzeAllMutants(specs, dataSet);

	}

	// !!!!still have bugs
	private void combineSession() {
		// List<String[]> newSs = new ArrayList<String[]>();
		for (int i = 0; i < usefulMutants.size(); i++) {
			String[] newSession = new String[3];
			int j = Character.getNumericValue(sessionName.charAt(sessionName.length() - 1)) + i + 1;
			newSession[0] = "session=parse_session" + j;
			newSession[1] = "operator=" + operatorString;
			// !!!have problem to get mutants path
			// ArrayList element:bubbleSort_AORB_13
			String[] tempFileName = usefulMutants.get(i).split("_");
			String opFileName = tempFileName[1] + "_" + tempFileName[2];
			newSession[2] = "basep=/Users/tracyTJR/Documents/workspace/muJavaPlugin/src/mujava/" + sessionName
					+ "/result/ArrayOperations/traditional_mutants/int_bubbleSort(int)/" + opFileName
					+ "/ArrayOperations.java";
			// newSs.add(newSession);
			mutantStack.push(newSession);
		}
	}

//	private void deleteCombinedMutantClass() {
//		// TODO Auto-generated method stub
//		String path = combinedMutantFile.getAbsolutePath();
//		String[] p = path.split("/");
//		p[p.length-1] = "CombinedMutants.class";
//		String newPath = "";
//		for(int i=0;i<p.length;i++){
//			if(i>=1){
//				newPath = newPath + "/"+p[i];
//			}else{
//				newPath = newPath + p[i];
//			}
//		}
//		System.out.println("path: "+path);
//		System.out.println("newPath: "+newPath);
//		
//		File file = new File(newPath);
//		if(file.exists()){
//			System.out.println(".class file is found, start to delete...");
//			boolean d = file.delete();
//			System.out.println("class deleted: "+d);
//		}
//	}
	
	public void execute(String[] args) {

		// Plugin plugin = new Plugin();

		/// mutantStack.push(args);
		/// while (!mutantStack.isEmpty()) {

		// mutantStack.pop();

		Thread.currentThread().setName("Main-Thread");

		parseArgs(args);// parse args, involves stack and queue storage.

		generateMutantsUsingMuJava();

		combineMutantMethodsInSingleJavaFile();

		compileAll();

		analyze();// return usefull mutants and store them into
					// ArrayList<String> usefulMutants

//		deleteCombinedMutantClass();//donnot need to delete .class file
		
		
		/// combineSession();

		// List<String[]> combinedSession = combineSession();
		// int x = 0;
		// while(!combinedSession.isEmpty()){
		// mutantStack.push(combinedSession.get(x));
		// }

		/// execute((String[])mutantStack.pop());

	}

	

	// System.out.println("====Plugin has been received by
	// usefulMutants=====");
	// for(int i=0;i<usefulMutants.size();i++){
	// System.out.println(usefulMutants.get(i));
	// System.out.println("");
	// }

	// return usefulMutants;
	/*
	 * new basep: /Users/tracyTJR/Documents/workspace/muJavaPlugin/src/mujava/
	 * parse_session2/result/ArrayOperations/traditional_mutants/
	 * int_bubbleSort(int)/AORB_8/ArrayOperations.java
	 * 
	 * returned value: bubbleSort_AORB_8
	 */

}
