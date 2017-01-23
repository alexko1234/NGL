package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.sra.instance.Run;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.lang3.StringUtils;

//import com.sun.xml.internal.ws.api.pipe.NextAction;




import fr.cea.ig.MongoDBDAO;
import play.Logger;
import validation.ContextValidation;


public class RepriseHistorique {
	
	public static String adminComment = "Creation dans le cadre d'une reprise d'historique"; 
	
	
	
	public static List<Sample> xmlToSample(File xmlFile) {
		List<Sample> listSamples = new ArrayList<Sample>();

		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * Etape 3 : création d'un Document
			 */
			
			final Document document= builder.parse(xmlFile);
			//Affiche du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			//System.out.println("\n*************RACINE************");
			//System.out.println(racine.getNodeName());
			/*
			 * Etape 5 : récupération des samples
			 */
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();

			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element eltSample = (Element) racineNoeuds.item(i);
					//Affichage d'un sample
					//System.out.println("\n*************SAMPLE************");
					//System.out.println("alias : " + eltSample.getAttribute("alias"));
					
					String alias = eltSample.getAttribute("alias");
					Sample sample = new Sample();
					//System.out.println("sample alias : " + alias);
					sample.code = alias;
					
					String accession = eltSample.getAttribute("accession");
					if (StringUtils.isNotBlank(accession)){
						sample.accession = accession;
						//System.out.println("sample alias : " + alias);
					}
					if (eltSample.getElementsByTagName("DESCRIPTION").item(0) != null) {
						String description =  eltSample.getElementsByTagName("DESCRIPTION").item(0).getTextContent();
						if (StringUtils.isNotBlank(description)) {
							sample.description = description;
							//System.out.println("description : " + description);
						}
					}
					if (eltSample.getElementsByTagName("TITLE").item(0) != null) {
						String title =  eltSample.getElementsByTagName("TITLE").item(0).getTextContent();
						if (StringUtils.isNotBlank(title)) {
							sample.title = title;
							//System.out.println("title : " + title);
						}
					}
					
					final Element eltSampleName = (Element) eltSample.getElementsByTagName("SAMPLE_NAME").item(0);
					if (eltSampleName.getElementsByTagName("COMMON_NAME").item(0)!= null) {
						String commonName = eltSampleName.getElementsByTagName("COMMON_NAME").item(0).getTextContent();
						if (StringUtils.isNotBlank(commonName)) {
							sample.commonName = commonName;
							//System.out.println("commonName : " + commonName);
						}
					}
					if (eltSampleName.getElementsByTagName("SCIENTIFIC_NAME").item(0)!= null) {
						String scientificName = eltSampleName.getElementsByTagName("SCIENTIFIC_NAME").item(0).getTextContent();
						if (StringUtils.isNotBlank(scientificName)) {
							sample.scientificName = scientificName;
							//System.out.println("scientificName : " + scientificName);
						}
					}
					if (eltSampleName.getElementsByTagName("ANONYMIZED_NAME").item(0)!= null) {
						String anonymizedName = eltSampleName.getElementsByTagName("ANONYMIZED_NAME").item(0).getTextContent();
						if (StringUtils.isNotBlank(anonymizedName)) {
							sample.anonymizedName = anonymizedName;
							System.out.println("anonymizedName : " + anonymizedName);
						}
					}
					if (eltSampleName.getElementsByTagName("TAXON_ID").item(0)!= null) {
						if (StringUtils.isNotBlank(eltSampleName.getElementsByTagName("TAXON_ID").item(0).getTextContent())){
							int taxon_id = new Integer(eltSampleName.getElementsByTagName("TAXON_ID").item(0).getTextContent()).intValue();
							sample.taxonId = new Integer(taxon_id);
							//System.out.println("taxon_id  : " + taxon_id);
							
						}
					}
					if (!listSamples.contains(sample)){
							listSamples.add(sample);
					} 
				}
			} // end for  
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} 
		return listSamples;
	}	
	
	
	public static List<Study> xmlToStudy(File xmlFile) {
		List<Study> listStudies = new ArrayList<Study>();

		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * Etape 3 : création d'un Document
			 */
			
			final Document document= builder.parse(xmlFile);
			//Affiche du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
			/*
			 * Etape 5 : récupération des samples
			 */
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();

			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element eltStudy = (Element) racineNoeuds.item(i);
					//Affichage d'un study
					System.out.println("\n*************STUDY************");
					System.out.println("alias : " + eltStudy.getAttribute("alias"));
					
					String alias = eltStudy.getAttribute("alias");
					Study study = new Study();
					System.out.println("study alias : " + alias);
					study.code = alias;
					
					String accession = eltStudy.getAttribute("accession");
					if (StringUtils.isNotBlank(accession)){
						study.accession = accession;
						System.out.println("study alias : " + alias);
					}
					
					final Element descriptor = (Element) eltStudy.getElementsByTagName("DESCRIPTOR").item(0);
					
					if (descriptor.getElementsByTagName("STUDY_TITLE").item(0)!= null) {
						String title = descriptor.getElementsByTagName("STUDY_TITLE").item(0).getTextContent();
						if (StringUtils.isNotBlank(title)) {
							study.title = title;
							System.out.println("title : " + title);
						}
					}
					if (descriptor.getElementsByTagName("STUDY_ABSTRACT").item(0)!= null) {
						String studyAbstract = descriptor.getElementsByTagName("STUDY_ABSTRACT").item(0).getTextContent();
						if (StringUtils.isNotBlank(studyAbstract)) {
							study.studyAbstract = studyAbstract;
							System.out.println("studyAbstract : " + studyAbstract);
						}
					}
					if (descriptor.getElementsByTagName("STUDY_DESCRIPTION").item(0)!= null) {
						String description = descriptor.getElementsByTagName("STUDY_DESCRIPTION").item(0).getTextContent();
						if (StringUtils.isNotBlank(description)) {
							study.description = description;
							System.out.println("description : " + description);
						}
					}	
					if (descriptor.getElementsByTagName("CENTER_PROJECT_NAME").item(0)!= null) {
						String centerProjectName = descriptor.getElementsByTagName("CENTER_PROJECT_NAME").item(0).getTextContent();
						if (StringUtils.isNotBlank(centerProjectName)) {
							study.centerProjectName = centerProjectName;
							System.out.println("centerProjectName : " + centerProjectName);
						}
					}	
					if (descriptor.getElementsByTagName("STUDY_TYPE").item(0)!= null) {
						final Element eltStudyType = (Element) descriptor.getElementsByTagName("STUDY_TYPE").item(0);
						String existingStudyType = eltStudyType.getAttribute("existing_study_type");
						if (StringUtils.isNotBlank(existingStudyType)) {
							study.existingStudyType = existingStudyType;
						}
					}	
					if (!listStudies.contains(study)){
						listStudies.add(study);
					}
				} 
				
			} // end for  
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} 
		return listStudies;
	}	
	
	public static List<Experiment> xmlToExperiment(File xmlFile) {
		List<Experiment> listExperiments = new ArrayList<Experiment>();

		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * Etape 3 : création d'un Document
			 */

			final Document document= builder.parse(xmlFile);
			//Affiche du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
			/*
			 * Etape 5 : récupération des experiments
			 */
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();

			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element eltExperiment = (Element) racineNoeuds.item(i);
					//Affichage d'un experiment
					//System.out.println("\n*************EXPERIMENT************");
					//System.out.println("alias : " + eltExperiment.getAttribute("alias"));

					String alias = eltExperiment.getAttribute("alias");
					Experiment experiment = new Experiment();
					//System.out.println("experiment alias : " + alias);
					experiment.code = alias;

					experiment.readSetCode = experiment.code.replace("exp_", "");
					//System.out.println("experiment.readSetCode = " + experiment.readSetCode);
					String accession = eltExperiment.getAttribute("accession");
					if (StringUtils.isNotBlank(accession)){
						experiment.accession = accession;
						//System.out.println("experiment accession : " + accession);
					}

					if (eltExperiment.getElementsByTagName("TITLE").item(0) != null) {
						String title =  eltExperiment.getElementsByTagName("TITLE").item(0).getTextContent();
						if (StringUtils.isNotBlank(title)) {
							experiment.title = title;
							//System.out.println("title : " + title);
						}
					}
					final Element eltExpStudyRef = (Element) eltExperiment.getElementsByTagName("STUDY_REF").item(0);
					String study_refname = eltExpStudyRef.getAttribute("refname");
					if (StringUtils.isNotBlank(study_refname)) {
						experiment.studyCode = study_refname;
						//System.out.println("study_refname : " + study_refname);
					}
					String study_accession = eltExpStudyRef.getAttribute("accession");
					// A completer : normalement uniquement reference du studyCode au niveau de experiment ?????
					// todo
					if (StringUtils.isNotBlank(study_accession)) {
						experiment.studyAccession = study_accession;
						//System.out.println("study_accession : " + study_accession);
					}
					final Element eltExpPlatform = (Element) eltExperiment.getElementsByTagName("PLATFORM").item(0);
					NodeList nodesTypePlatform = eltExpPlatform.getChildNodes();
					String typePlatform = "";
					for (int l=0; l<nodesTypePlatform.getLength(); l++) {
						Node child = nodesTypePlatform.item(l);
						if (child instanceof Element){
							//System.out.println("hello à l'indide " + l + " tag=" + nodesTypePlatform.item(l).getNodeName());
							typePlatform = nodesTypePlatform.item(l).getNodeName();
						}
					}

					if ("ILLUMINA".equalsIgnoreCase(typePlatform) || "LS454".equalsIgnoreCase(typePlatform) || "OXFORD_NANOPORE".equalsIgnoreCase(typePlatform)) {
						experiment.typePlatform = typePlatform;
						final Element eltExpPlatformType = (Element) eltExpPlatform.getElementsByTagName(typePlatform).item(0);
						if (eltExpPlatformType.getElementsByTagName("INSTRUMENT_MODEL").item(0) != null) {	
							String instrument_model = eltExpPlatformType.getElementsByTagName("INSTRUMENT_MODEL").item(0).getTextContent();
							if (StringUtils.isNotBlank(instrument_model)){
								//System.out.println("instrument_model : " + instrument_model);
								experiment.instrumentModel = instrument_model;
							}
						}
					} else {
						System.out.println("Type de plateforme inconnue" + typePlatform );
					}


					final Element eltExpDesign = (Element) eltExperiment.getElementsByTagName("DESIGN").item(0);

					final Element eltExpSampleDescriptor = (Element) eltExpDesign.getElementsByTagName("SAMPLE_DESCRIPTOR").item(0);
					String sample_refname = eltExpSampleDescriptor.getAttribute("refname");
					if (StringUtils.isNotBlank(sample_refname)) {
						experiment.sampleCode = sample_refname;
						//System.out.println("sample_refname : " + sample_refname);
					}
					String sample_accession = eltExpSampleDescriptor.getAttribute("accession");
					// A completer : normalement uniquement reference du sampleCode au niveau de experiment ?????
					// todo
					if (StringUtils.isNotBlank(sample_accession)) {
						experiment.sampleAccession = sample_accession;
						//System.out.println("sample_accession : " + sample_accession);
					}

					final Element eltExpLibraryDescriptor = (Element) eltExpDesign.getElementsByTagName("LIBRARY_DESCRIPTOR").item(0);

					if (eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_NAME").item(0) != null) {
						String libraryName =  eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_NAME").item(0).getTextContent();
						if (StringUtils.isNotBlank(libraryName)) {
							experiment.libraryName = libraryName;
							//System.out.println("libraryName : " + libraryName);
						}
					}

					if (eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_STRATEGY").item(0) != null) {
						String libraryStrategy =  eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_STRATEGY").item(0).getTextContent();
						if (StringUtils.isNotBlank(libraryStrategy)) {
							experiment.libraryStrategy = libraryStrategy;
							//System.out.println("libraryStrategy : " + libraryStrategy);
						}
					}

					if (eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_SOURCE").item(0) != null) {
						String librarySource =  eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_SOURCE").item(0).getTextContent();
						if (StringUtils.isNotBlank(librarySource)) {
							experiment.librarySource = librarySource;
							//System.out.println("librarySource : " + librarySource);
						}
					}


					if (eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_SELECTION").item(0) != null) {
						String librarySelection =  eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_SELECTION").item(0).getTextContent();
						if (StringUtils.isNotBlank(librarySelection)) {
							experiment.librarySelection = librarySelection;
							//System.out.println("librarySelection : " + librarySelection);
						}
					}

					if (eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_CONSTRUCTION_PROTOCOL").item(0) != null) {
						String libraryConstructionProtocol =  eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_CONSTRUCTION_PROTOCOL").item(0).getTextContent();
						if (StringUtils.isNotBlank(libraryConstructionProtocol)) {
							experiment.libraryConstructionProtocol = libraryConstructionProtocol;
							//System.out.println("libraryConstructionProtocol : " + libraryConstructionProtocol);
						}
					}	

					final Element eltExpLibraryLayout= (Element) eltExpLibraryDescriptor.getElementsByTagName("LIBRARY_LAYOUT").item(0);
					if (eltExpLibraryLayout.getElementsByTagName("SINGLE").item(0) != null) {
						//System.out.println("libraryLayout : single");
						experiment.libraryLayout = "single";
					} 
					if (eltExpLibraryLayout.getElementsByTagName("PAIRED").item(0) != null) {
						//System.out.println("libraryLayout : paired");
						experiment.libraryLayout = "paired";
						final Element eltExpLibraryLayoutPaired = (Element) eltExpLibraryLayout.getElementsByTagName("PAIRED").item(0);

						String nominal_length = eltExpLibraryLayoutPaired.getAttribute("NOMINAL_LENGTH");
						if (StringUtils.isNotBlank(nominal_length)){
							//System.out.println("nominal_length : " + nominal_length);
							experiment.libraryLayoutNominalLength = new Integer(nominal_length);
						}	
					} 

					final Element eltSpotDescriptor = (Element) eltExpDesign.getElementsByTagName("SPOT_DESCRIPTOR").item(0);
					if (eltSpotDescriptor != null) {
						final Element eltSpotDecodeSpec = (Element) eltSpotDescriptor.getElementsByTagName("SPOT_DECODE_SPEC").item(0);
						if (eltSpotDecodeSpec != null) {
							if (eltSpotDecodeSpec.getElementsByTagName("SPOT_LENGTH").item(0) != null) {
								String spotLength = eltSpotDecodeSpec.getElementsByTagName("SPOT_LENGTH").item(0).getTextContent();
								if (StringUtils.isNotBlank(spotLength)) {
									//System.out.println("spotLength : " + spotLength);
									experiment.spotLength = new Long(spotLength);
								}
							}

							final NodeList racine_read_spec = eltSpotDecodeSpec.getElementsByTagName("READ_SPEC");
							final int nb_read_spec = racine_read_spec.getLength();
							experiment.readSpecs = new ArrayList<ReadSpec>();
							String readLabel_1 = "";
							for (int j = 0; j<nb_read_spec; j++) {
								ReadSpec readSpec = new ReadSpec();
								if(racine_read_spec.item(j).getNodeType() == Node.ELEMENT_NODE) {
									final Element eltReadSpec = (Element) racine_read_spec.item(j);

									if (eltReadSpec.getElementsByTagName("READ_INDEX").item(0) != null) {
										String readIndex = eltReadSpec.getElementsByTagName("READ_INDEX").item(0).getTextContent();
										if (StringUtils.isNotBlank(readIndex)) {
											//System.out.println("readIndex : " + readIndex);
											readSpec.readIndex = new Integer(readIndex);									
										}
									}
									if (eltReadSpec.getElementsByTagName("READ_LABEL").item(0) != null) {
										String readLabel = eltReadSpec.getElementsByTagName("READ_LABEL").item(0).getTextContent();
										if (StringUtils.isNotBlank(readLabel)) {
											//System.out.println("readLabel : " + readLabel);
											readSpec.readLabel = readLabel;									

											if (j==1){
												readLabel_1 = readLabel;
											}
											if (j==2) {
												if (readLabel_1.equals("F") && readLabel.equals("R")){
													experiment.libraryLayoutOrientation="forward-reverse";
												}
												if (readLabel_1.equals("R") && readLabel.equals("F")){
													experiment.libraryLayoutOrientation="reverse-forward";
												}
											}

										}
									}
									if (eltReadSpec.getElementsByTagName("READ_CLASS").item(0) != null) {
										String readClass = eltReadSpec.getElementsByTagName("READ_CLASS").item(0).getTextContent();
										if (StringUtils.isNotBlank(readClass)) {
											//System.out.println("readClass : " + readClass);
											readSpec.readClass = readClass;									
										}
									}
									if (eltReadSpec.getElementsByTagName("READ_TYPE").item(0) != null) {
										String readType = eltReadSpec.getElementsByTagName("READ_TYPE").item(0).getTextContent();
										if (StringUtils.isNotBlank(readType)) {
											//System.out.println("readType : " + readType);
											readSpec.readType = readType;									
										}
									}
									if (eltReadSpec.getElementsByTagName("BASE_COORD").item(0) != null) {
										String baseCoord = eltReadSpec.getElementsByTagName("BASE_COORD").item(0).getTextContent();
										if (StringUtils.isNotBlank(baseCoord)) {
											//System.out.println("baseCoord : " + baseCoord);
											readSpec.baseCoord = new Integer(baseCoord);									
										}
										if (j==2) {
											experiment.lastBaseCoord = readSpec.baseCoord;
										}
									}
									// Ajout basecall dans le cas plateforme LS454
									if (eltReadSpec.getElementsByTagName("EXPECTED_BASECALL_TABLE").item(0) != null) {
										//System.out.println("OKKKKKK, EXPECTED_BASECALL_TABLE");
										final Element eltExpectedBasecallTable = (Element) eltReadSpec.getElementsByTagName("EXPECTED_BASECALL_TABLE").item(0);
										final NodeList racine_basecall = eltExpectedBasecallTable.getElementsByTagName("BASECALL");

										if (racine_basecall != null) {
											//System.out.println("OKK BASECALL");
											final int nb_baseCall = racine_basecall.getLength();
											//System.out.println("nb_baseCall = " + nb_baseCall);
											for (int k = 0; k<nb_baseCall; k++) {
												Node nodeBaseCall = racine_basecall.item(k);
												//System.out.println("k=" + k);
												if(nodeBaseCall instanceof Element) {
													final Element eltBasecall = (Element) nodeBaseCall;
													String basecall = eltBasecall.getTextContent();// racine_basecall.getElementsByTagName("BASECALL").item(k).getTextContent();
													//System.out.println("BASECALL ="+ basecall);
													readSpec.expectedBaseCallTable.add(basecall);
													//System.out.println("ok pour ajout BASECALL="+ basecall);
												}
											}
										}
									} //end if expectedBasecall
									experiment.readSpecs.add(readSpec);
								} // end if readspec
							} // end for readspec
						} // end if (eltSpotDecodeSpec == null) {

					}
					// Dans le cas des Illumina Single :
					if(experiment.typePlatform.equalsIgnoreCase("ILLUMINA") && experiment.libraryLayout.equalsIgnoreCase("SINGLE")){
						if (experiment.readSpecs.size() == 1){	
							if("Forward".equalsIgnoreCase(experiment.readSpecs.get(0).readType)){
								experiment.libraryLayoutOrientation = "Forward";
							}
						}
					}
					// Dans le cas des Illumina Paired
					if(experiment.typePlatform.equalsIgnoreCase("ILLUMINA") && experiment.libraryLayout.equalsIgnoreCase("PAIRED")){
						// Determiner experiment.lastBaseCoord à partir des readSpec.baseCoord :
						if (experiment.readSpecs.size() == 2){
							if(experiment.readSpecs.get(1).baseCoord >= experiment.readSpecs.get(0).baseCoord) {
								experiment.lastBaseCoord = experiment.readSpecs.get(1).baseCoord;
							} else {
								experiment.lastBaseCoord = experiment.readSpecs.get(0).baseCoord;
							}
							//System.out.println("EXPERIMENT.lastBaseCoordonnée = " + experiment.lastBaseCoord);
						}

						// Determiner experiment.libraryLayoutOrientation à partir des readSpec.READ_LABEL
						if (experiment.readSpecs.size() == 2){
							if("F".equalsIgnoreCase(experiment.readSpecs.get(0).readLabel) &&  "R".equalsIgnoreCase(experiment.readSpecs.get(1).readLabel)) {
								experiment.libraryLayoutOrientation="forward-reverse";
							} 
							if("R".equalsIgnoreCase(experiment.readSpecs.get(0).readLabel) &&  "F".equalsIgnoreCase(experiment.readSpecs.get(1).readLabel)) {
								experiment.libraryLayoutOrientation="reverse-forward";
							} 
							if("F".equalsIgnoreCase(experiment.readSpecs.get(0).readLabel) &&  "F".equalsIgnoreCase(experiment.readSpecs.get(1).readLabel)) {
								experiment.libraryLayoutOrientation="forward-forward";
							} 
							if("R".equalsIgnoreCase(experiment.readSpecs.get(0).readLabel) &&  "R".equalsIgnoreCase(experiment.readSpecs.get(1).readLabel)) {
								experiment.libraryLayoutOrientation="reverse-reverse";
							} 
							//System.out.println("experiment.libraryLayoutOrientation = " + experiment.libraryLayoutOrientation);
						}
					} 

					// Dans le cas des LS454 Single :
					if(experiment.typePlatform.equalsIgnoreCase("LS454") && experiment.libraryLayout.equalsIgnoreCase("SINGLE")){
						if (experiment.readSpecs.size() == 2){	
							if("Forward".equalsIgnoreCase(experiment.readSpecs.get(1).readType)){
								experiment.libraryLayoutOrientation = "Forward";
							}
						}
					}
					// Dans le cas des LS454 Paired :
					if(experiment.typePlatform.equalsIgnoreCase("LS454") && experiment.libraryLayout.equalsIgnoreCase("PAIRED")){
						/*System.out.println("experiment.readSpecs.get(0).readType="+experiment.readSpecs.get(0).readType);
						System.out.println("experiment.readSpecs.get(1).readType="+experiment.readSpecs.get(1).readType);
						System.out.println("experiment.readSpecs.get(2).readType="+experiment.readSpecs.get(2).readType);
						System.out.println("experiment.readSpecs.get(3).readType="+experiment.readSpecs.get(3).readType);
						 */
						// Determiner experiment.libraryLayoutOrientation à partir des readSpec.READ_TYPE
						if (experiment.readSpecs.size() == 4){
							if("Forward".equals(experiment.readSpecs.get(1).readType) &&  "Reverse".equalsIgnoreCase(experiment.readSpecs.get(3).readType)){
								experiment.libraryLayoutOrientation="forward-reverse";
							} 
							if("Reverse".equals(experiment.readSpecs.get(1).readType) &&  "Forward".equalsIgnoreCase(experiment.readSpecs.get(3).readType)){
								experiment.libraryLayoutOrientation="reverse-forward";
							} 
							if("Forward".equals(experiment.readSpecs.get(1).readType) &&  "Forward".equalsIgnoreCase(experiment.readSpecs.get(3).readType)){
								experiment.libraryLayoutOrientation="forward-forward";
							} 
							if("Reverse".equals(experiment.readSpecs.get(1).readType) &&  "Reverse".equalsIgnoreCase(experiment.readSpecs.get(3).readType)){
								experiment.libraryLayoutOrientation="reverse-reverse";
							}
							//System.out.println("experiment.libraryLayoutOrientation = " + experiment.libraryLayoutOrientation);
						}
					}
					if (!listExperiments.contains(experiment)){
						//System.out.println ("Ajout de " + experiment.code + " dans listExperiments");
						listExperiments.add(experiment);
					} // end if eltSpotDescriptor

				} // end if nodeExperiment

			} // end for  experiment
			//System.out.println ("sortie de forExperiment dans xml2experiment");
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} 
		return listExperiments;
	}	

	public static List<Run> xmlToRun(File xmlFile) {
		List<Run> listRuns = new ArrayList<Run>();
		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * Etape 3 : création d'un Document
			 */
			
			final Document document= builder.parse(xmlFile);
			//Affiche du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
			/*
			 * Etape 5 : récupération des runs
			 */
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();

			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element eltRun = (Element) racineNoeuds.item(i);
					//Affichage d'un run
					System.out.println("\n*************RUN************");
					System.out.println("alias : " + eltRun.getAttribute("alias"));
					
					String alias = eltRun.getAttribute("alias");
					Run run = new Run();
					System.out.println("run alias : " + alias);
					run.code = alias;
					String accession = eltRun.getAttribute("accession");
					if (StringUtils.isNotBlank(accession)){
						run.accession = accession;
						System.out.println("run alias : " + alias);
					}
					String dateInString = eltRun.getAttribute("run_date");
					System.out.println("run date : " + dateInString);
					dateInString = dateInString.substring(0, 10);

					if (StringUtils.isNotBlank(dateInString)){
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
						Date date = formatter.parse(dateInString);				
						System.out.println("run dateInString : " + dateInString);
						System.out.println("run date : " + date);
						run.runDate = date;

					}
					
					String runCenter = eltRun.getAttribute("run_center");
					if (StringUtils.isNotBlank(runCenter)){
						run.runCenter = runCenter;
						System.out.println("run center : " + runCenter);
					}
					
					final Element eltExpRef = (Element) eltRun.getElementsByTagName("EXPERIMENT_REF").item(0);
					String exp_accession = eltExpRef.getAttribute("accession");
					
					String exp_refname = eltExpRef.getAttribute("refname");
					if (StringUtils.isNotBlank(exp_accession)) {
						run.expAccession = exp_accession;
					}
					if (StringUtils.isNotBlank(exp_refname)) {
						run.expCode = exp_refname;
						//System.out.println("experiment_refname : " + exp_refname);
					} else {
						if (StringUtils.isNotBlank(exp_accession)) {
							Experiment experiment = MongoDBDAO.findOne(InstanceConstants.SRA_EXPERIMENT_COLL_NAME,
									Experiment.class, DBQuery.and(DBQuery.is("accession", exp_accession)));
							if (experiment != null) {
								run.expCode = experiment.code; 
							} else {
								run.expCode = run.code;
								run.expCode = run.expCode.replaceFirst("run_", "exp_");
							}
						}
					}
					
					
					final Element eltDataBlock = (Element) eltRun.getElementsByTagName("DATA_BLOCK").item(0);
					final Element eltFiles = (Element) eltDataBlock.getElementsByTagName("FILES").item(0);

					final NodeList racine_files = eltDataBlock.getElementsByTagName("FILE");
					if (racine_files != null) {
						final int nb_files = racine_files.getLength();
						//System.out.println("nb_files = " + nb_files);
						for (int k = 0; k<nb_files; k++) {
							Node nodeFile = racine_files.item(k);
							//System.out.println("k=" + k);
							if(nodeFile instanceof Element) {
								final Element eltFile = (Element) nodeFile;
								String filename = eltFile.getAttribute("filename");
								String filetype = eltFile.getAttribute("filetype");
								String checksum_method = eltFile.getAttribute("checksum_method");
								String md5 = eltFile.getAttribute("checksum");
								RawData rawData = new RawData();
								rawData.relatifName = filename;
								rawData.md5 = md5;
								rawData.extention = filetype;
								run.listRawData.add(rawData);
							}
						}
					}
					if (!listRuns.contains(run)){
						System.out.println ("Ajout de " + run.code + " dans listRuns");
						listRuns.add(run);
					} 
				} 
			}
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return listRuns;
	}


	public static Map<String, String> loadAc(File file_list_AC) throws IOException, SraException {
		if (file_list_AC == null) {
			throw new SraException("Absence du nom du fichier des AC " );
		}
		if (! file_list_AC.exists()){
			throw new SraException("Fichier des AC non present sur disque : "+ file_list_AC.getAbsolutePath());
		}

		BufferedReader inputBuffer = null;
		try {
			inputBuffer = new BufferedReader(new FileReader(file_list_AC));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String lg = null;

		Map<String, String> mapAC = new HashMap<String, String>();
		
		boolean reportEbi = false;
		while ((lg = inputBuffer.readLine()) != null) {
			if (lg.startsWith("<?")){
				// ignorer 
				System.out.println("ligne ignoree A= '"+ lg+"'");
			} else if (! lg.matches("^\\s*<.*")) {
				// ignorer 
				System.out.println("ligne ignoree B = '"+ lg+"'");

			} else if (lg.matches("^\\s*$")) {
				// ignorer 
				System.out.println("ligne ignoree C= '"+ lg+"'");

			} else {
				if (reportEbi==false) {
					System.out.println("reportEbi = false");
					System.out.println("ligne = '"+ lg+"'");
					String pattern_string = "<RECEIPT\\s+receiptDate=\"(\\S+)\"\\s+submissionFile=\"(\\S+)\"\\s+success=\"true\"";
					//String pattern_string = "<RECEIPT";
					java.util.regex.Pattern pattern = Pattern.compile(pattern_string);
					Matcher m = pattern.matcher(lg);
					/*if ( ! m.find() ) {
						throw new SraException("Absence de la ligne RECEIPT dans le fichier " + file_list_AC.getAbsolutePath());
					} */
					reportEbi = true;
					System.out.println("reportEbi = true");

				} else {
					
				
				//System.out.println("Traitement des AC :");
				String patternAc = "<(\\S+)\\s+accession=\"(\\S+)\"\\s+alias=\"(\\S+)\"";
				java.util.regex.Pattern pAc = Pattern.compile(patternAc);

				
					System.out.println(lg);
					Matcher mAc = pAc.matcher(lg);
					// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
					if ( ! mAc.find() ) {
						// autre ligne que AC.
					} else {
						//System.out.println("type='"+mAc.group(1)+"', accession='"+mAc.group(2)+"', alias='"+ mAc.group(3)+"'" );
						if (mAc.group(1).equalsIgnoreCase("RUN")){
							//System.out.println("insertion dans mapRun de "+ mAc.group(3) + " et "+ mAc.group(2));
							mapAC.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("EXPERIMENT")){
							//System.out.println("insertion dans mapExperiment de " + mAc.group(3) + " et "+ mAc.group(2));
							mapAC.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("SAMPLE")){
							//System.out.println("insertion dans mapSample de " + mAc.group(3) + " et "+ mAc.group(2));
							mapAC.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("STUDY")){
							mapAC.put(mAc.group(3), mAc.group(2));
							//System.out.println("insertion dans mapStudy de " + mAc.group(3) + " et "+ mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("SUBMISSION")){
							mapAC.put(mAc.group(3), mAc.group(2));
							//System.out.println("insertion dans mapSubmission de "  + mAc.group(3) + " et "+ mAc.group(2));
						} else {

						}
					}// end else
				} // end else
			}
		}
		inputBuffer.close();
		return mapAC;
	}

	// Renvoie la liste des samples generés à partir des fichiers xml et completés pour AC, state ...
	public List<Sample> forSamples(File xmlSample, String user) throws IOException, SraException {	
		// Recuperer ensemble des objets samples à partir du fichier xml
		System.out.println("Recuperation des samples");
		List<Sample> listSamples = xmlToSample(xmlSample);
		System.out.println("Recuperation des AC");

		String pattern = "sample_([A-Z]{2,3})(_|-)";
		String pattern2 = "TARA_([A-Z]{2,3})(_|-)"; // pour recuperer les samples du projet tara declare par nous dans le projet BCB ou ALP
		
		java.util.regex.Pattern p = Pattern.compile(pattern);
		java.util.regex.Pattern p2 = Pattern.compile(pattern2);
		
		
		for (Sample sample : listSamples) {
			//System.out.println("!!!!!!!!!!!!!!!!!!sample : '" + sample.code + "'");
			//if ( ! mapAc.containsKey(sample.code)) {
			//	throw new SraException("Absence de numeros d'accession pour le code '" + sample.code + "'");
			//}
			//System.out.println("Pour le sample : " + sample.code + " numeros d'accession = " + mapAc.get(sample.code));
			// ajouter informations codeProject, accession , status et traceInformation et sauver dans base;
			
			Matcher m = p.matcher(sample.code);
			
			
			// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
			if ( m.find() ) {
				sample.projectCode = m.group(1);
			} else {
				m = p2.matcher(sample.code);
				if ( m.find() ) {
					sample.projectCode = m.group(1);
				}
			}
		
			
			//sample.accession = mapAc.get(sample.code);
			sample.state = new State("F-SUB", user);
			sample.traceInformation.setTraceInformation(user);
			ContextValidation contextValidation = new ContextValidation(user);
			contextValidation.setCreationMode();
			contextValidation.getContextObjects().put("type", "sra");
			sample.adminComment = adminComment;
			
		}
		System.out.println("sortie de forSamples");
		
		return listSamples;
	}

	// Renvoie la liste des studies generés à partir des fichiers xml et completés pour AC, state ...
	public List<Study> forStudies(File xmlStudy, String user) throws IOException, SraException {	
		// Recuperer ensemble des objets study à partir du fichier xml
		System.out.println("Recuperation des study");
		List<Study> listStudies = xmlToStudy(xmlStudy);

		String pattern1 = "^study_([A-Z]{2,3})$";
		String pattern2 = "^study_([A-Z]{2,3})(_|-)";
		String pattern3 = "^study_([A-Z]{2,3})$";

		java.util.regex.Pattern p1 = Pattern.compile(pattern1);
		java.util.regex.Pattern p2 = Pattern.compile(pattern2);
		java.util.regex.Pattern p3 = Pattern.compile(pattern3);

		for (Study study : listStudies) {
			System.out.println("!!!!!!!!!!!!!!!!!!study : '" + study.code + "'");

			Matcher m = p1.matcher(study.code);
			// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
			if ( m.find() ) {
				study.projectCode = m.group(1);
			} else {
				m = p2.matcher(study.code);
				if ( m.find() ) {
					study.projectCode = m.group(1);
				} else {
					m = p3.matcher(study.code);
					if ( m.find() ) {
						study.projectCode = m.group(1);
					}
				}
			}
	
			study.state = new State("F-SUB", user);
			study.traceInformation.setTraceInformation(user);
			ContextValidation contextValidation = new ContextValidation(user);
			contextValidation.setCreationMode();
			contextValidation.getContextObjects().put("type", "sra");
			study.adminComment = adminComment;
	
		}
		System.out.println("sortie de forStudies");

		return listStudies;
}

	// Renvoie la liste des experiments generés à partir des fichiers xml et completés pour AC, state ...
	public List<Experiment> forExperiments(File xmlExperiment, String user) throws IOException, SraException {	
		// Recuperer ensemble des objets experiment à partir du fichier xml
		System.out.println("Recuperation des experiment");
		List<Experiment> listExperiments = xmlToExperiment(xmlExperiment);
		String pattern1 = "^exp_([A-Z]{2,3})_";
		String pattern2 = "^exp_[0-9]\\.TCA\\.([A-Z]{2,3})_";
		String pattern3 = "^exp_[0-9]\\.GAC\\.([A-Z]{2,3})_";
		//String pattern3 = "^study_([A-Z]{3})$";
		//String pattern4 = "^study_([A-Z]{2})$";

		java.util.regex.Pattern p1 = Pattern.compile(pattern1);
		java.util.regex.Pattern p2 = Pattern.compile(pattern2);
		java.util.regex.Pattern p3 = Pattern.compile(pattern3);
		//java.util.regex.Pattern p4 = Pattern.compile(pattern4);
		
		for (Experiment experiment : listExperiments) {
			//System.out.println("experiment : " + experiment.code);
			// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
			Matcher m = p1.matcher(experiment.code);
			if ( m.find() ) {
				experiment.projectCode = m.group(1);
			} else {
				m = p2.matcher(experiment.code);
				if ( m.find() ) {
					experiment.projectCode = m.group(1);
				} else {
					m = p3.matcher(experiment.code);
					if ( m.find() ) {
						experiment.projectCode = m.group(1);
					}/*else {
						m = p4.matcher(experiment.code);
						if ( m.find() ) {
							experiment.projectCode = m.group(1);
						}
					}*/
				}
			}
			experiment.state = new State("F-SUB", user);
			experiment.traceInformation.setTraceInformation(user);
			experiment.adminComment = adminComment;
			/*
			ContextValidation contextValidation = new ContextValidation(user);
			contextValidation.setCreationMode();
			contextValidation.getContextObjects().put("type", "sra");
			*/
		}
		System.out.println("sortie de forExperiments");
		return listExperiments;
	}

	// Renvoie la liste des runs generés à partir des fichiers xml et completés pour AC, state ...
	public List<Run> forRuns(File xmlRun, String user) throws IOException, SraException {	
		// Recuperer ensemble des objets experiment à partir du fichier xml
		List<Run> listRuns = xmlToRun(xmlRun);
		System.out.println("Recuperation de "+ listRuns.size() + " runs");

		for (Run run : listRuns) {
			//System.out.println("run : " + run.code);
			run.adminComment = adminComment;
		}
		System.out.println("sortie de forRuns");
		return listRuns;
	}

	// Creation des samples à partir du fichier xml, ajout de l'AC, state, ....puis validation et 
	// sauvegarde de tous les samples ssi valides.
	/*public static void main(final String[] args) throws IOException, SraException {

		File xmlSample = new File("/env/cns/submit_traces/SRA/NGL_test/test_xml/sample.xml");
		File acSampleFile = new File("/env/cns/submit_traces/SRA/NGL_test/test_xml/acFile.xml");
		String projectCode = "BCZ";
		String user = "william";
		RepriseHistorique repriseHistorique = new RepriseHistorique();

		List<Sample> listSamples = repriseHistorique.forSamples(xmlSample, acSampleFile, projectCode, user);
		System.out.println("retour dans repriseHistoriqueSamplesTest");
		// Verifier la validité des samples
		for (Sample sample : listSamples) {
			System.out.println("dans repriseHistoriqueSamplesTest => sample : " + sample.code);
			ContextValidation contextValidation = new ContextValidation(user);
			contextValidation.setCreationMode();
			contextValidation.getContextObjects().put("type", "sra");
			sample.validate(contextValidation);
			System.out.println("\ndisplayErrors pour validationSample:" + sample.code);
			if (contextValidation.errors.size()==0) {
				System.out.println("Sample "+ sample.code + " valide ");
			} else {
				contextValidation.displayErrors(Logger.of("SRA"));
				throw new SraException("Sample " + sample.code + " non valide");
			}
		}
		// Sauver tous les samples
		for (Sample sample : listSamples) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, AbstractSample.class, "code", sample.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sample);
				System.out.println ("ok pour sauvegarde dans la base du sample " + sample.code);
			}
		}

	}*/

}

