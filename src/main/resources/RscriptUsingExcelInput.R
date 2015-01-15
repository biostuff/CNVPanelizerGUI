# install.packages("C:/Users/OliveiraCristiano/Downloads/codeFromThomas/Rstudio/AmpCNVrstudio_0.1.zip")
#library(AmpCNVrstudio)
library(CNVPanelizer)


args <- commandArgs(TRUE)

inputDataFilepath <- "D:\\repository\\mixedJavaAndGroovy\\output\\inputData.xlsx"
bedFilepath <- "D:\\repository\\mixedJavaAndGroovy\\testData\\bed\\LCPv1(CNV).bed"
ampliconColumnNumber <-  4
removePcrDuplicates <- TRUE
outputDirectory <- "D:\\repository\\mixedJavaAndGroovy\\output"
numberOfBootstrapReplicates <- 10000
replicates <- numberOfBootstrapReplicates


#inputDataFilepath <- "D:\\repository\\mixedJavaAndGroovy\\output\\inputData.xlsx"
#sampleDirectory <- "D:\\repository\\mixedJavaAndGroovy\\testData\\samples"
#bedFilepath <- "D:\\repository\\mixedJavaAndGroovy\\testData\\bed\\LCPv1(CNV).bed"
#ampliconColumnNumber <-  4
#removePcrDuplicates <- TRUE
#outputDirectory <- "D:\\repository\\mixedJavaAndGroovy\\outputStatic"
#numberOfBootstrapReplicates <- 10000
#replicates <- numberOfBootstrapReplicates

inputDataFilepath <- args[1]
bedFilepath <- args[2]
ampliconColumnNumber <-  as.integer(args[3])
removePcrDuplicates <- as.logical(args[4])
outputDirectory <- args[5]
numberOfBootstrapReplicates <- 10000
replicates <- numberOfBootstrapReplicates

print(paste0("inputDataFilepath                :  ", inputDataFilepath))
#print(paste0("sampleDirectory                   :  ", sampleDirectory))
print(paste0("bedFilepath                       :  ", bedFilepath))
print(paste0("ampliconColumnNumber              :  ", ampliconColumnNumber))
print(paste0("removePcrDuplicates               :  ", removePcrDuplicates))
print(paste0("outputDirectory                   :  ", outputDirectory))
print(paste0("replicates                        :  ", replicates))

referenceFilenames <- ReadXLSXToList(inputDataFilepath)$reference[, 1]
sampleFilenames <- ReadXLSXToList(inputDataFilepath)$sample[, 1]

#referenceFilenames <- list.files(path = referenceDirectory, pattern = ".bam$", full.names = TRUE)
#sampleFilenames <- list.files(path = sampleDirectory, pattern = ".bam$", full.names = TRUE)

########################################################################################################
# count the reads in the bedfile defined regions
########################################################################################################

#extract the information from a bed file
#genomicRangesFromBed <- BedToGenomicRanges(bedFolder, panel, ampliconColumn = 4, split = "_")
genomicRangesFromBed <- BedToGenomicRanges(bedFilepath, ampliconColumn = ampliconColumnNumber , split = "_")

print(genomicRangesFromBed)

print("_______________________________________________________________")
print(elementMetadata(genomicRangesFromBed))

print("_______________________________________________________________")

metadataFromGenomicRanges <- elementMetadata(genomicRangesFromBed)

print(metadataFromGenomicRanges)
print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")




geneNames = metadataFromGenomicRanges["geneNames"][,1]
ampliconNames = metadataFromGenomicRanges["ampliconNames"][,1]



print(geneNames)
print(ampliconNames)


















#count the reads in the files of interest

#for the reference
print("going to read referencen read counts..x.")
referenceReadCounts <- ReadCountsFromBam(referenceFilenames, sampleNames = referenceFilenames, genomicRangesFromBed, ampliconNames = ampliconNames, removeDup = removePcrDuplicates)
#print(referenceReadCounts)
print("Done!")


#for the samples
sampleReadCounts <- ReadCountsFromBam(sampleFilenames, sampleNames = sampleFilenames, genomicRangesFromBed, ampliconNames = ampliconNames, removeDup = removePcrDuplicates)
#print(sampleReadCounts)
print("Read the data correctly!")


print("going to normalize...")
normalizedReadCounts <- CombinedNormalizedCounts(sampleReadCounts, referenceReadCounts, genomicRangesFromBed, amplicons = ampliconNames)
print(normalizedReadCounts)
print("normalized!")


################################################################################################################
# perform the bootstrap based analysis
################################################################################################################

#define a sample matrix
#samplesNormalizedReadCounts = normalizedReadCounts samples
samplesNormalizedReadCounts = normalizedReadCounts["samples"][[1]]


#define a matrix with the sample you want to be tested
#referenceNormalizedReadCounts = normalizedReadCounts reference
referenceNormalizedReadCounts = normalizedReadCounts["reference"][[1]]
print("Have normalized datasets..")



#get the genes positions in the matrix as a list from a gene name vector
#genesPositionsIndex = indexGenesPositions(geneNames)

#calculate the bootstrap sampling
#bootList = BootList(genesPositionsIndex, samplesNormalizedReadCounts, referenceNormalizedReadCounts, reps = replicates, refWeights = NULL)


print(geneNames)
#print(samplesNormalizedReadCounts)
#print(referenceNormalizedReadCounts)
print(replicates)
print("[[[[[[[[[[[[[[[[[[[[[[[[")
print("going to bootstrap...")


bootList <- BootList(geneNames,
                     samplesNormalizedReadCounts,
                     referenceNormalizedReadCounts,
                     reps = replicates,
                     refWeights = NULL)

print(bootList)






#########################################################################################
#estimate the background noise left after normalization
#########################################################################################






#calc the background object for each sample produces a list list for each sample ampl_num_combination
#calc_background_object(unique_amplicon_numbers,ratio_mat,replicates = 1000,probs = ampl_weights) 

#iterateAmplNum(uniqueAmpliconNumbers, ratioMatrix[unlist(genePosNonSig[[i]]), i], replicates = 10000, probs = amplWeights[[i]])

#calculate the significance from the bootstrap sampling
sigList = CheckSignificance(bootList)
print(sigList)

backgroundNoise <- Background(geneNames, samplesNormalizedReadCounts, referenceNormalizedReadCounts, sigList, replicates = replicates)
print(backgroundNoise)



reportTables <- ReportTables(bootList, geneNames, backgroundNoise, referenceNormalizedReadCounts, samplesNormalizedReadCounts)
print(reportTables)

#reportTablesFilepath <- paste0(outputDirectory, "\\", "reportTables.xlsx")
reportTablesFilepath <- filepath(outputDirectory, "reportTables.xlsx")
WriteListToXLSX(reportTables, reportTablesFilepath)

# WriteXLS("reportTables", paste0(outputDirectory,"report_tables.xlsx"),AdjWidth = TRUE, BoldHeaderRow = TRUE, row.names = TRUE,col.names = TRUE)
# Export read counts to excel format
#readCountsFilepath <- "D:\\repository\\mixedJavaAndGroovy\\outputStatic2\\readCounts.xlsx"
#readCountsFilepath <- paste0(outputDirectory, "\\", "readCounts.xlsx")
readCountsFilepath <- file.path(outputDirectory, "readCounts.xlsx")


#normalizedReadCountsFilepath <- paste0(outputDirectory, "\\", "normalizedReadCounts.xlsx")
normalizedReadCountsFilepath <- file.path(outputDirectory, "normalizedReadCounts.xlsx")

print(paste0("Writing read counts to ", readCountsFilepath))
#WriteReadCountsToXLSX(sampleReadCounts, referenceReadCounts, readCountsFilepath)
#WriteReadCountsToXLSX(samplesNormalizedReadCounts, referenceNormalizedReadCounts, normalizedReadCountsFilepath)

WriteListToXLSX(list(samplesReadCount = sampleReadCounts, referenceReadCounts = referenceNormalizedReadCounts), readCountsFilepath)
WriteListToXLSX(list(samplesReadCount = samplesNormalizedReadCounts, referenceReadCounts = referenceNormalizedReadCounts), normalizedReadCountsFilepath)



print("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{")
PlotBootstrapDistributions(bootList, reportTables, outputDirectory)







print("THE END!")









doSomething <- function(){
  Sys.sleep(5)
  print('done!')
}

# doSomething()


#dir.create(paste0(outputDirectory, "/what"), showWarnings = FALSE)
