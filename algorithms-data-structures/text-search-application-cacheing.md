2-level cache system for text search applications
============================================
Most of the web applications have a cache component (key-value based). 
This cache will hold the frequently used requests/responses and serve from the cache for matching requests rather than processing them again (hence quicker response).

The most important design decision of such cache system is the items to be cached, 
because it has direct impact on the size of the cache line item implies cache size (how many items cache can hold). 
Bigger the cache size better will be the cache hit rate.

In the context of text search based applications, both the key and value of cache entry are strings (plain text items). 
In most of these applications, you can observe lot of redundancy in both the key and value items. 
We are attacking the redundancy inside the cache to achieve bigger size and hence better cache hit rate.

## cache subsystem

The new cache subsystem contains 2 main components:

1. 2-way indexed table storing all possible keywords, bag of words, sentences.
2. Simple key-value based LRU cache (storing indexes of the above table).

Since, the cache is going be a key-value pair of only indexes, we can store lots of cache entries compared to a key-value pair of strings. 
Thus, avoiding the redundancy of texts inside cache. On a high level the proposed cache subsystem works as below.


Whenever a cache reference is made,

1. Hash of the given ‘keyword/bag of words/sentence’ is calculated
2. Hash is used to refer in the 2-way indexed table
3. If an entry is found

    a. Index of the entry is obtained from 2-way indexed table
    
    b. LRU cache is referred for the matched index
    
    c. Value indexes are referred back in 2-way indexed table and strings are returned
    
4. In case of miss, actual processing is done and cache is updated with the new entry

    a. Hash for the ‘keyword/bag of words/sentence’ is calculated
    
    b. New entry in the table is created for key
    
    b. The value is logically broken in to multiple pieces
   
    d. Hash for the values is calculated and referred in the table
    
    e. New entry in the table is created for missing values
    
    f. LRU cache is populated with the new ‘key’ and ‘list of values’ indexes
