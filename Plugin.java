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
	private Queue<String[]> mutantQueue = new LinkedList<String[]>();

	private List<?>[] usefulMutants = new List<?>[3];

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
		 * @modifier Jiarui Tian replace the static combined mutant file name
		 * with dynamic file name reason: If the name of the files which needed
		 * to be compiled are same, JVM code cache only load the first compiled
		 * file
		 */
		String combinedMutantFileName = sessionName + "CombinedMutants";
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
	private void combineSession() {
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < usefulMutants[j].size(); i++) {
				String[] newSession = new String[3];
				int l = Character.getNumericValue(sessionName.charAt(sessionName.length() - 1)) + i + 1;
				newSession[0] = "session=parse_session" + l;
				newSession[1] = "operator=" + operatorString;
				String[] tempFileName = ((String) usefulMutants[j].get(i)).split("_");
				String opFileName = tempFileName[1] + "_" + tempFileName[2];
				// change first part of below string
				newSession[2] = "basep=/Users/tracyTJR/Documents/workspace/muJavaPlugin/src/mujava/" + sessionName
						+ "/result/ArrayOperations/traditional_mutants/int_bubbleSort(int)/" + opFileName
						+ "/ArrayOperations.java";

				mutantStack.push(newSession);
				System.out.println("newSession is: " + newSession[0].toString() + "," + newSession[1].toString() + ","
						+ newSession[2].toString());
			}
		}
	}

	/*
	 * check if top two mutants are same, if same, means it will lead to
	 * everlasting loop and all generated mutants will be same
	 */

	public void execute(String[] args) {

		// Plugin plugin = new Plugin();

		mutantStack.push(args);
		while (!mutantStack.isEmpty()) {

			String[] executeSession = mutantStack.pop();
			mutantQueue.add(executeSession);

			Thread.currentThread().setName("Main-Thread");

			parseArgs(executeSession);// parse args, involves stack and
										// queue storage.

			generateMutantsUsingMuJava();

			combineMutantMethodsInSingleJavaFile();

			compileAll();

			analyze();// return usefull mutants and store them into
						// ArrayList<String> usefulMutants

			combineSession();
			if(usefulMutants[2].size()>0){
				break;
			}
			
		}
	}

}


