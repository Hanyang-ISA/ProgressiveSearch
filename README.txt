#Download
Download ProgressiveSearch.jar and SampleData.zip in the same directory and unzip SampleData.zip.
ProgressiveSearch     ProgressiveSearch.jar    (64-bit Linux on x86 CPU)
Sample data               SampleData.zip
Source code               ProgressiveSearch(Source).zip


#Usage
###1. Comet-P
java -jar ProgressiveSearch.jar  Comet-P  <oldDB_result>  <oldDB>  <newDB>  <Spectrum>  <Comet_param>  <out_dir>

( Use this command for the sample data: java -jar ProgressiveSearch.jar Comet-P oldDB_result.txt uniprot_2020_01_withDecoy.fasta uniprot_2020_02_withDecoy.fasta sample.mgf comet.params output_Comet-P )


###2. Comet-E
java -jar ProgressiveSearch.jar  Comet-E  <oldDB_result>  <oldDB_result_histogram>  <oldDB>  <newDB>  <Spectrum>  <Comet_param>  <out_dir>

( Use this command for the sample data: java -jar ProgressiveSearch.jar Comet-E oldDB_result.txt oldDB_result_histogram.txt uniprot_2020_01_withDecoy.fasta uniprot_2020_02_withDecoy.fasta sample.mgf comet.params output_Comet-E )



##Parameter descriptions
<oldDB_result>:  Comet result of the old DB
<oldDB_result_histogram>:  Comet histogram of old DB result
<oldDB>:  old DB (.fasta)
<newDB>:  new DB (.fasta)
<Spectrum>:  spectrum set (.mgf)
<Comet_param>:  Comet parameters (.params)
<out_dir>:  output directory


##Output files in <out_dir>
Comet-P_result.txt:  result of Comet-P
Comet-E_result.txt:  result of Comet-E (Comet-E only)
deleted.fasta:  deleted DB
shared.fasta:  shared DB
inserted.fasta:  inserted DB
deletion_result.txt:  Comet result of the shared DB for extracted spectra
deletedDB_result.txt:  Comet result of the deleted DB (Comet-E only)
insertedDB_result.txt:  Comet result of the inserted DB
<Spectrum>_extracted.mgf:  spectrum set for deletion
deletion_result_histogram.txt:  Comet histogram of shared DB result for extracted spectra (Comet-E only)
deletedDB_result_histogram.txt:  Comet histogram of deleted DB result (Comet-E only)
insertedDB_result_histogram.txt:  Comet histogram of inserted DB result (Comet-E only)
newDB_result_histogram.txt:  Comet histogram of new DB result (Comet-E only)

For Comet_Param options in detail, refer to https://uwpr.github.io/Comet/parameters/.