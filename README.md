jpmorgan-test1
==============

Coding Quetion asked during interview with JP morgan


Interview Starge Status : Passed


Feel free to read/use it and suggest better solution.



Problem : 
Input : List of DownloadRequest and Optimum batch Size
Output : Batches of Download Request based on Group Id


DownLoadRequest has following fields
    private final AccountID accountID;
    private final GroupID groupID;

groupid can be null.

Batches need to be created with following rules
1) All DownloadRequest with Same Group must be in same Batch
2) Two or more GroupId DwnloadRequest can be in same group if total numbe rof items in bacth doesnt go above optimum size of batch
3) DownloadRequest with null groupid can go into any batch and can be distributed in N batches. But  size of batch must not go above optimum size of batch.
4) batch Size can go above optimum size only if one kind of DownloadRequest with same Group id are already more then Optimum size


We need to code Batcher in this problem.



