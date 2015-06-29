library(CNVPanelizer)

args <- commandArgs(TRUE)

inputDataFilepath <- args[1]
bedFilepath <- args[2]
ampliconColumnNumber <-  as.integer(args[3])
removePcrDuplicates <- as.logical(args[4])
numberOfBootstrapReplicates <- as.integer(args[5])
replicates <- numberOfBootstrapReplicates
specificityLevel <- as.integer(args[6])
outputDirectory <- args[7]
plotsOutputDirectory <- file.path(outputDirectory, "plots")
tablesOutputDirectory <- file.path(outputDirectory, "tables")

print(paste0("inputDataFilepath                :  ", inputDataFilepath))
#print(paste0("sampleDirectory                   :  ", sampleDirectory))
print(paste0("bedFilepath                       :  ", bedFilepath))
print(paste0("ampliconColumnNumber              :  ", ampliconColumnNumber))
print(paste0("removePcrDuplicates               :  ", removePcrDuplicates))
print(paste0("outputDirectory                   :  ", outputDirectory))
print(paste0("replicates                        :  ", replicates))
print(paste0("plotsOutputDirectory                        :  ", plotsOutputDirectory))
print(paste0("tablesOutputDirectory                        :  ", tablesOutputDirectory))

referenceFilenames <- ReadXLSXToList(inputDataFilepath)$reference[, 1]
sampleFilenames <- ReadXLSXToList(inputDataFilepath)$sample[, 1]

#referenceFilenames <- list.files(path = referenceDirectory, pattern = ".bam$", full.names = TRUE)
#sampleFilenames <- list.files(path = sampleDirectory, pattern = ".bam$", full.names = TRUE)

########################################################################################################
# count the reads in the bedfile defined regions
########################################################################################################


########################################################################################################
message("Retrieve the bedfile defined regions")
########################################################################################################
#extract the information from a bed file
genomicRangesFromBed <- BedToGenomicRanges(bedFilepath,
                                           ampliconColumn = ampliconColumnNumber,
                                           split = "_")

metadataFromGenomicRanges <- elementMetadata(genomicRangesFromBed)
geneNames = metadataFromGenomicRanges["geneNames"][,1]
ampliconNames = metadataFromGenomicRanges["ampliconNames"][,1]

########################################################################################################
message("Retrieve the data and normalize it")
########################################################################################################
#count the reads in the files of interest
#for the reference and for the samples
referenceReadCounts <- ReadCountsFromBam(referenceFilenames,
                                         sampleNames = referenceFilenames,
                                         genomicRangesFromBed,
                                         ampliconNames = ampliconNames,
                                         removeDup = removePcrDuplicates)

sampleReadCounts <- ReadCountsFromBam(sampleFilenames,
                                      sampleNames = sampleFilenames,
                                      genomicRangesFromBed,
                                      ampliconNames = ampliconNames,
                                      removeDup = removePcrDuplicates)

# Normalize references and samples together
normalizedReadCounts <- CombinedNormalizedCounts(sampleReadCounts,
                                                 referenceReadCounts,
                                                 ampliconNames = ampliconNames)

########################################################################################################
message("perform the bootstrap based analysis")
########################################################################################################
# After normalization data sets need to be splitted again to perform bootstrap
samplesNormalizedReadCounts = normalizedReadCounts["samples"][[1]]
referenceNormalizedReadCounts = normalizedReadCounts["reference"][[1]]

# Perform the bootstrap based analysis
bootList <- BootList(geneNames,
                     samplesNormalizedReadCounts,
                     referenceNormalizedReadCounts,
                     replicates = replicates)

backgroundNoise <- Background(geneNames,
                              samplesNormalizedReadCounts,
                              referenceNormalizedReadCounts,
                              bootList,
                              specificityLevel,
                              replicates = replicates)

reportTables <- ReportTables(geneNames,
                             samplesNormalizedReadCounts,
                             referenceNormalizedReadCounts,
                             bootList,
                             backgroundNoise)



reportTablesFilepath <- file.path(outputDirectory, "reportTables.xlsx")
WriteListToXLSX(reportTables, reportTablesFilepath)

readCountsFilepath <- file.path(outputDirectory, "readCounts.xlsx")

normalizedReadCountsFilepath <- file.path(outputDirectory, "normalizedReadCounts.xlsx")

WriteListToXLSX(list(samplesReadCount = sampleReadCounts, referenceReadCounts = referenceNormalizedReadCounts), readCountsFilepath)
WriteListToXLSX(list(samplesReadCount = samplesNormalizedReadCounts, referenceReadCounts = referenceNormalizedReadCounts), normalizedReadCountsFilepath)


########################################################################################################
message("Plotting bootstrap distributions")
########################################################################################################

PlotBootstrapDistributions(bootList,
                           reportTables,
                           outputFolder = plotsOutputDirectory,
                           save = TRUE)
message("end")
