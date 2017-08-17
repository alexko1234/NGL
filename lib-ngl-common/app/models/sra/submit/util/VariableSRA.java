package models.sra.submit.util;



import java.util.HashMap;
import java.util.Map;

import play.Play;

public interface VariableSRA {
	
	static final String centerName = "GSC";
	static final String laboratoryName = "Genoscope - CEA";
	static final String submissionRootDirectory = Play.application().configuration().getString("submissionRootDirectory");
	static final String libraryConstructionProtocol = "none provided";
	static final String admin = "william";
	static final String xmlSubmission = "submission.xml";
	static final String xmlStudys = "study.xml";
	static final String xmlSamples = "sample.xml";
	static final String xmlExperiments = "experiment.xml";
	static final String xmlRuns = "run.xml";
	static final String resultSendXml = "resultEbi_codeSubmission.xml";
	
	static final Map<String, String> mapCenterName =  new HashMap<String, String>() {
		{
			put("gsc", "GSC"); 
		}
	};
	static final Map<String, String> mapLaboratoryName =  new HashMap<String, String>() {
		{
			put("genoscope - cea", "Genoscope - CEA"); 
		}
	};
	
	static final Map<String, String> mapTypeReadset =  new HashMap<String, String>() {
		{
			put("illumina", "illumina"); // Si pas de sample à creer parce que fournis par les collaborateurs
			put("nanopore", "nanopore"); // si sample specifique par code_projet et taxon
			put("ls454", "ls454"); // si sample specifique par code_projet et clone
		}
	};
	
	static final Map<String, String> mapStrategySample =  new HashMap<String, String>() {
		{
			put("strategy_external_sample", "strategy_external_sample"); // Si pas de sample à creer parce que fournis par les collaborateurs
			put("strategy_sample_taxon", "strategy_sample_taxon"); // si sample specifique par code_projet et taxon
			put("strategy_sample_clone", "strategy_sample_clone"); // si sample specifique par code_projet et clone
		}
	};
	
	static final Map<String, String> mapStrategyStudy =  new HashMap<String, String>() {
		{
			put("strategy_external_study", "strategy_external_study"); // Si pas de study à creer parce que fournis par les collaborateurs
			put("strategy_internal_study", "strategy_internal_study"); 
		}
	};	
	/*
	static final Map<String, String> mapStatus = new HashMap<String, String>() {
		{  
			put("new", "new"); 
			put("uservalidate", "userValidate"); // value_database, label.
			put("inwaiting", "inWaiting"); 
			put("inprogress", "inProgress");
			put("submitted", "submitted");
			
		}
	};  
	
	static final Map<String, String> mapExternalStatus = new HashMap<String, String>() {
		{
			put("submitted", "submitted");
			
		}
	};*/ 	
	
	static final Map<String, String> mapExistingStudyType = new HashMap<String, String>() {
		{
			put("whole genome sequencing", "Whole Genome Sequencing"); // value_database, label=value_ebi.
			put("metagenomics", "Metagenomics");
			put("transcriptome analysis", "Transcriptome Analysis");
			put("epigenetics","Epigenetics");
			put("synthetic genomics","Synthetic Genomics");
			put("forensic or paleo-genomics","Forensic or Paleo-genomics");
			put("gene regulation study","Gene Regulation Study");
			put("cancer genomics","Cancer Genomics");
			put("population genomics","Population Genomics");
			put("rnaseq","RNASeq");
			put("exome sequencing","Exome Sequencing");
			put("pooled clone sequencing","Pooled Clone Sequencing");
			put("other", "Other");
		}
	};  
	
	static final Map<String, String> mapLibraryStrategy = new HashMap<String, String>() {
		{
			put("wgs", "WGS"); 
			put("wga", "WGA"); 
			put("wxs", "WXS");
			put("rna-seq","RNA-Seq");
			put("mirna-seq","miRNA-Seq");
			put("ncrna-seq","ncRNA-Seq");
			put("wcs", "WCS"); 
			put("clone", "CLONE"); 
			put("poolclone", "POOLCLONE"); 
			put("amplicon", "AMPLICON"); 
			put("cloneend", "CLONEEND"); 
			put("finishing", "FINISHING"); 
			put("chip-seq", "ChIP-Seq");
			put("mnase-seq", "MNase-Seq");
			put("dnase-hypersensitivity", "DNase-Hypersensitivity");
			put("bisulfite-seq", "Bisulfite-Seq");
			put("est", "EST");
			put("fl-cdna", "FL-cDNA");
			put("cts", "CTS");
			put("mre-seq", "MRE-Seq");
			put("medip-seq", "MeDIP-Seq");
			put("mbd-seq", "MBD-Seq");
			put("tn-seq", "Tn-Seq");
			put("validation","VALIDATION");
			put("faire-seq","FAIRE-seq");			
			put("selex","SELEX");
			put("rip-seq","RIP-Seq");
			put("chia-pet","ChIA-PET");
			put("other", "OTHER");
		}
	};  
	
	static final Map<String, String> mapLibrarySource = new HashMap<String, String>() {
		{
			put("genomic", "GENOMIC"); 
			put("transcriptomic", "TRANSCRIPTOMIC");
			put("metagenomic", "METAGENOMIC"); 
			put("metatranscriptomic", "METATRANSCRIPTOMIC");
			put("synthetic", "SYNTHETIC");
			put("viral rna", "VIRAL RNA");
			put("other", "OTHER");
		}
	};  
	
	static final Map<String, String> mapLibrarySelection = new HashMap<String, String>() {
		{
			put("random", "RANDOM"); 
			put("pcr", "PCR");
			put("random pcr", "RANDOM PCR"); 
			put("rt-pcr", "RT-PCR");
			put("synthetic", "SYNTHETIC");
			put("hmpr", "HMPR");
			put("mf","MF");
			put("repeat fractionation","repeat fractionation");
			put("size fractionation","size fractionation");
			put("msll","MSLL");
			put("cdna","cDNA");
			put("chip","ChIP");
			put("mnase","MNase");
			put("dnase","DNAse");
			put("hybrid selection","Hybrid Selection");
			put("reduced representation","Reduced Representation");
			put("restriction digest","Restriction Digest");
			put("5-methylcytidine antibody","5-methylcytidine antibody");
			put("mbd2 protein methyl-cpg binding domain","MBD2 protein methyl-CpG binding domain");
			put("cage", "CAGE");
			put("race", "RACE");
			put("mda", "MDA");
			put("padlock probes capture method","padlock probes capture method");
			put("other", "other");
			put("unspecified", "unspecified");
		}
	};
	
	
	static final Map<String, String> mapLibraryLayout = new HashMap<String, String>() {
		{
			put("single", "SINGLE"); 
			put("paired", "PAIRED");
		}
	}; 
	
	static final Map<String, String> mapLibraryLayoutOrientation = new HashMap<String, String>() {
		{
			put("forward", "Forward"); 
			put("forward-reverse", "Forward-Reverse");
			put("reverse-forward", "Reverse-Forward");	
			put("forward-forward", "Forward-Forward");	

		}
	};  
	
	
	static final Map<String, String> mapTypePlatform = new HashMap<String, String>() {
		{
			put("illumina", "ILLUMINA"); 
			put("ls454","LS454");  // pour les reprises d'historique, il existe des ls454
			put("oxford_nanopore","OXFORD_NANOPORE");
		}
	};  
	static final Map<String, String> mapInstrumentModel = new HashMap<String, String>() {
		{	// instrument model pour optical_mapping:
			put("argus",null); // pas prevus de soumettre ces données

			// instrument model pour oxford_nanopore
			put("minion", "MinION");
			put("mk1", "MinION");
			put("mk1b", "MinION");
			
			// instrument model pour L454
			put("454 gs 20", "454 GS 20"); 
			put("454 gs flx", "454 GS FLX");
			put("454 gs flx titanium", "454 GS FLX Titanium");
			put("454 gs flx+", "454 GS FLX+");		
			
			// type instrument model pour illumina dans sra version 1.5 : 
			put("illumina genome analyzer","Illumina Genome Analyzer");
			put("illumina ga","Illumina Genome Analyzer");
			put("ga","Illumina Genome Analyzer");
			put("illumina genome analyzer ii","Illumina Genome Analyzer II");
			put("illumina gaii","Illumina Genome Analyzer II");
			put("gaii","Illumina Genome Analyzer II");
			put("illumina genome analyzer iix","Illumina Genome Analyzer IIx");
			put("illumina gaiix","Illumina Genome Analyzer IIx");
			put("gaiix","Illumina Genome Analyzer IIx");
			put("illumina hiseq 2500","Illumina HiSeq 2500");
			put("hiseq2500","Illumina HiSeq 2500");
			put("illumina hiseq 2000","Illumina HiSeq 2000");
			put("hiseq2000","Illumina HiSeq 2000");
			put("illumina hiseq 2000","Illumina HiSeq 2000");
			put("illumina hiseq 1500","Illumina HiSeq 1500");
			put("hiseq1500","Illumina HiSeq 1500");
			put("illumina hiseq 1000","Illumina HiSeq 1000");
			put("hiseq1000","Illumina HiSeq 1000");
			put("illumina miseq","Illumina MiSeq");
			put("miseq","Illumina MiSeq");
			put("illumina hiscansq","Illumina HiScanSQ");
			put("hiscansq","Illumina HiScanSQ");
			put("hiseq x ten","HiSeq X Ten");
			put("nextseq","NextSeq 500");
			put("illumina hiseq 4000","Illumina HiSeq 4000");
			
			// correspondance nomCnsInstrumentModel et instrumentModel :
			put("rgaiix","Illumina Genome Analyzer IIx");
			put("rhs2000","Illumina HiSeq 2000");
			put("rhs2500","Illumina HiSeq 2500");
			put("rhs2500r","Illumina HiSeq 2500");
			put("rmiseq","Illumina MiSeq");
			put("hiseq4000","Illumina HiSeq 4000");
			put("rhs4000","Illumina HiSeq 4000");

			put ("unspecified", "unspecified");  // ajout pour repriseHistorique.
		}
	};  
	
	/*static final Map<String, String> mapCnsToInstrumentModel = new HashMap<String, String>() {
		{
    put("RGAIIx","Illumina Genome Analyzer IIx");
    put("RHS2000","Illumina HiSeq 2000");
    put("RHS2500","Illumina HiSeq 2500");
    put("RHS2500R","Illumina HiSeq 2500");
   	put("RMISEQ","Illumina MiSeq");
		}
	};
	*/

	static final Map<String, String> mapAnalysisFileType =  new HashMap<String, String>() {
		{
			put("fasta", "fasta"); 
			put("contig_fasta", "contig_fasta"); 
			put("contig_flatfile", "contig_flatfile"); 
			put("scaffold_fasta", "scaffold_fasta"); 
			put("scaffold_flatfile", "scaffold_flatfile"); 
			put("scaffold_agp", "scaffold_agp"); 
			put("chromosome_fasta", "chromosome_fasta"); 
			put("chromosome_flatfile", "chromosome_flatfile"); 
			put("chromosome_agp", "chromosome_agp"); 
			put("chromosome_list", "chromosome_list"); 
			put("unlocalised_contig_list", "unlocalised_contig_list"); 
			put("unlocalised_scaffold_list", "unlocalised_scaffold_list"); 
			put("other", "other"); 			
		}
	};
	

	
	
};


