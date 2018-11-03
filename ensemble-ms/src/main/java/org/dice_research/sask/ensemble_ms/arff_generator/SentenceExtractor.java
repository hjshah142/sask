package org.dice_research.sask.ensemble_ms.arff_generator;



import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;

/**
 * This class take data from oke folder folder sort them and provide it to
 * different extractors and store response from extractors
 * 
 * @author Harsh Shah
 */

public class SentenceExtractor {

	static File path = new File("C:\\Users\\harsh\\Downloads\\Ensemble Data\\oke data");
	static File[] files = path.listFiles();
	static List<String> sentences = new ArrayList<String>();
	static File file = new File("C:\\Users\\harsh\\\\MLdata\\traindata.arff");
   static File file2 = new File("C:\\Users\\harsh\\\\MLdata\\trainNewdata2.arff") ;	
	
	static FileWriter fileWriter;
	
	 static String sentence_data;

	public static void main(String[] args) {
		
		  

		


		try {
			fileWriter = new FileWriter(file);

			BufferedWriter bw = new BufferedWriter(fileWriter);
			// write in train data file for weka

			bw.write("@relation DataExtraction2");
			bw.newLine();
			bw.write("@ATTRIBUTE Sentence	string");
			bw.newLine();
			bw.write("@ATTRIBUTE Foxoutput	string");
			bw.newLine();
			bw.newLine();
			bw.write("@ATTRIBUTE OPENIEoutput	string");
			bw.newLine();
			bw.write("@data");
			bw.newLine();

			// sort the files in numerical order
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					String s1 = f1.getName().substring(5, f1.getName().indexOf("."));
					String s2 = f2.getName().substring(5, f2.getName().indexOf("."));
					return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
				}
			});
			ResponseRader();
			String fox_response_string = null;
			String openie_response_string = null;
			String sorokin_response_string = null;
			String cedric_response_string = null;

			for (int i = 0; i < 2; i++) 
			{
				if (files[i].isFile()) {

					String sentence = readLineByLine(files[i].toString());
					int x = sentence.indexOf("nif:isString   ");
					int start = x + 17;
					int end = sentence.indexOf("\" .");
					end--;
					sentence_data = sentence.substring(start, end);
					// System.out.println(sentence_data);
					sentences.add(sentence_data);
					
//				Extractors responses

					Map<String, Integer> port_vs_extractorMap = new HashMap<String, Integer>();
					port_vs_extractorMap.put("fox", 2222);
					port_vs_extractorMap.put("fred", 2223);
					port_vs_extractorMap.put("spotlight", 2224);
					port_vs_extractorMap.put("cedric", 2225);
					port_vs_extractorMap.put("openIE", 2226);
					port_vs_extractorMap.put("sorookin", 2227);

					// int portNumb[] = { 2222, 2224, 2225, 2226, 2227 };
					int portNumb[] = { 2222, 2226, 2227, 2225 };

					Map<String, String> foxRespMap = new HashMap<String, String>();
					Map<String, String> fredRespMap = new HashMap<String, String>();
					Map<String, String> spotlightRespMap = new HashMap<String, String>();
					Map<String, String> cedricRespMap = new HashMap<String, String>();
					Map<String, String> openIERespMap = new HashMap<String, String>();
					Map<String, String> sorookinRespMap = new HashMap<String, String>();

					// calling every extractors and getting outputs

					for (int j = 0; j < portNumb.length; j++) {
						// replacing url with space character
						String URL2 = java.net.URLEncoder.encode(sentences.get(i), "UTF-8").replace("+", "%20");
						// add extractor for url

						String _extractorURL = "http://localhost:" + portNumb[j] + "/extractSimple?input=" + URL2;

						// java.awt.Desktop.getDesktop().browse(java.net.URI.create(_extractorURL));
						// _extractorURL = "https://www.google.com/";

						URL url;
						try {
							url = new URL(_extractorURL);
							//
							HttpURLConnection con = (HttpURLConnection) url.openConnection();
							// System.out.println(url);
							con.setRequestMethod("GET");
							BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
							String inputLine;
							StringBuffer response = new StringBuffer();

							while ((inputLine = in.readLine()) != null) {

								response.append(inputLine);
							}

							in.close();

							// print result from response

							if (j == 0) 
							{
								List<String> fox_response_string_list = new ArrayList<String>();
								fox_response_string = response.toString();
								fox_response_string_list.add(fox_response_string);
							}

							else if (j == 1) 
							{
								fredRespMap.put(sentences.get(i), response.toString());
								// System.out.println(portNumb[j] + "----Extractors response--------------" + j
								// + " response");
								ArrayList<String> openie_response_string_list = new ArrayList<String>();
								openie_response_string = response.toString();
								openie_response_string_list.add(openie_response_string);
							}

							else if (j == 2) 
							{
								spotlightRespMap.put(sentences.get(i), response.toString());
								System.out.println(portNumb[j] + "----Extractors response--------------" + j + " response");
								ArrayList<String> sorokin_response_string_list = new ArrayList<String>();
								sorokin_response_string = response.toString();
								sorokin_response_string_list.add(sorokin_response_string);
								System.out.println(response.toString());
								System.out.println();
							}
							else if (j == 3) 
							{
								cedricRespMap.put(sentences.get(i), response.toString());
								System.out.println(
										portNumb[j] + "----Extractors response--------------" + j + " response");
								ArrayList<String> cedric_response_string_list = new ArrayList<String>();
								cedric_response_string = response.toString();
								cedric_response_string_list.add(cedric_response_string);								
								System.out.println(response.toString());
								System.out.println();
							}
							else if (j == 4) {
								openIERespMap.put(sentences.get(i), response.toString());
								System.out.println(
										portNumb[j] + "----Extractors response--------------" + j + " response");
								System.out.println(response.toString());
								System.out.println();
							}
							else if (j == 5) 
							{
								sorookinRespMap.put(sentences.get(i), response.toString());
							}

						}
						// end of try block
						catch (Exception e) {
							// TODO: handle exception
						}
					}
					TrainingFileWriter();
				String training_data = " ' " + sentences.get(i) + " ' " + ", " + "'" + fox_response_string + " ' "
							+ "," + " ' " + openie_response_string + " ' "  + "," + " ' " + sorokin_response_string + "," + " ' " + cedric_response_string+ " ' ";
					System.out.println(sentences.get(i));
					System.out.println(fox_response_string);
					System.out.println(openie_response_string);
					System.out.println(cedric_response_string);
					System.out.println(sorokin_response_string);
					System.out.println(training_data);
					try {

						bw.write(training_data);
						bw.newLine();

					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
			bw.close();
			fileWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

	public static String readLineByLine(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}
	public static void ResponseRader()
	{
		BasicConfigurator.configure();
        // create an empty model
		for (int i = 1; i < 100; i++) 
		{
		 final String inputFileName  = files[i].toString();
        Model model = ModelFactory.createDefaultModel();
        Model model2 = ModelFactory.createDefaultModel();
        
        
        InputStream in = FileManager.get().open( inputFileName );
        if (in == null)
        {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
        // read the RDF/XML file
//         read() method call is the URI which will be used for resolving relative URI's
//        model.read(in, "TURTLE");
        try
        {
        model.read(inputFileName) ;
        }
        catch(RiotException ro)
        {
        	ro.getMessage();
        }

        
//      to run in command line sparql.bat --data=vc-db-1.rdf --query=q1.rq               
        // write it to standard outString queryString = " .... " ;
		String queryString = 
				
			    "Prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			    + " CONSTRUCT {?s ?p ?o}" +
			    "WHERE {" +
			    "  ?ss rdf:subject ?s." +" ?ss rdf:predicate ?p." +" ?ss rdf:object ?o." +
			    "      }";
			 
        Query query = QueryFactory.create(queryString) ;
         
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) 
        {
        	
          Model results = qexec.execConstruct() ;
          
          System.out.println(results.toString());
       StmtIterator iter = results.listStatements();
       System.out.println(files[i].getName() + "  result after pasing sparql query.......");
          while(iter.hasNext())
          {
           System.out.println(iter.next());
          }
        }
        }
        
//        System.out.println("   Response after rdf read");
//     model.write(System.out,"TURTLE"); 
//     System.out.println(".............................");
//     model2.write(System.out,"TURTLE");

		
	}
	public static void TrainingFileWriter()
	{

//		List<String> sentences = new ArrayList<String>();

		FileWriter fileWriter;
		try 
		{
			fileWriter = new FileWriter(file2);
			BufferedWriter bw2 = new BufferedWriter(fileWriter);
			bw2.write("@relation DataExtraction2");
			bw2.newLine();
			bw2.write("@ATTRIBUTE Sentence	string");
			bw2.newLine();
			bw2.write("@ATTRIBUTE Foxoutput	string");
			bw2.newLine();
			bw2.newLine();
			bw2.write("@ATTRIBUTE OPENIEoutput	string");
			bw2.newLine();
			bw2.write("@data");
			bw2.newLine();
			bw2.close();
			fileWriter.close();
			System.out.println("File Created and closed");
	
			
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		// write in train data file for weka
		
	
		}
	

}
