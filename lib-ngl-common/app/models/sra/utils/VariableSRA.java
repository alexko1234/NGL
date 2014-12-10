package models.sra.utils;



import java.util.HashMap;
import java.util.Map;

public interface VariableSRA {
	
	static final String centerName = "GSC";
	static final String laboratoryName = "Genoscope - CEA";
	static final String submissionRootDirectory = "/env/cns/submit_traces/SRA/SNTS_output_xml";
	static final String libraryConstructionProtocol = "none provided";
	static final String admin = "william";
	
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
	static final Map<String, String> mapStrategySample =  new HashMap<String, String>() {
		{
			put("strategy_no_sample", "strategy_no_sample"); // Si pas de sample à creer parce que fournis par les collaborateurs
			put("strategy_sample_taxon", "strategy_sample_taxon"); // si sample specifique par code_projet et taxon
			put("strategy_sample_clone", "strategy_sample_clone"); // si sample specifique par code_projet et clone
		}
	};
	
	static final Map<String, String> mapExistingStudyType = new HashMap<String, String>() {
		{
			put("whole genome sequencing", "Whole Genome Sequencing"); 
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
		}
	};  
	
	
	static final Map<String, String> mapTypePlatform = new HashMap<String, String>() {
		{
			put("illumina", "ILLUMINA"); 
		}
	};  
	static final Map<String, String> mapInstrumentModel = new HashMap<String, String>() {
		{
			put("454 gs 20", "454 GS 20"); 
			put("454 gs flx", "454 GS FLX");
			put("454 gs flx titanium", "454 GS FLX Titanium");
			put("454 gs flx+", "454 GS FLX+");		
			put("illumina genome analyzer", "Illumina Genome Analyzer");
			put("illumina genome analyzer II", "Illumina Genome Analyzer II");
			put("illumina genome analyzer IIx", "Illumina Genome Analyzer IIx");
			put("illumina hiseq 2000", "Illumina HiSeq 2000");
			put("illumina miseq", "Illumina MiSeq");
			put("illumina hiseq 2500", "Illumina HiSeq 2500");
		}
	};  
		
}


