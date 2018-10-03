{:tags ("clean code" "database"), :title "Database Rant"
 :description
 "What is the primary purpose of the database? I won't blame you for saying data storage. In most software projects it is the place where we store our data. I will however tell you that you are wrong. "
}
-----
**What is the primary purpose of the database?**

I won't blame you for saying data storage. In most software projects it is the place where we store our data. I will however tell you that you are wrong.

**Data storage is not the primary purpose of the database**

If you want to just store data, why add the overhead of a database? Why not just save your data directly on the file system.

> *My client wants an application! Applications generate data, therefore we must have a database! We will use SQL cause it is what we know! Now that we know how we will do it, let's ask the client what his problem is.*

What is the real problem here? Why is blatantly choosing a relational database based on the perception that we are writing relational data wrong? It is wrong because data storage is not the problem that databases solve. Why not just store everything in a large json file?

**Because it is hard to query!**

And in that statement lies the true purpose of your database. It is not about storage. It is about data consumption. Storage is an implementation detail. Your database stores your data in a certain state, so you can query that state in a specific way. Why choose relational over document or graph over search engine? Because each one is good at a different type of query (And typically the others are not good at that type of query). Thus we should stop choosing databases because we think they fit a storage model. We should choose them based on the query problems we have. Do I need to just read blobs of data or do I have olap operations or do I want to understand how people are related to each other (or do I have all of these)?

I'll concede that databases are typically damn good at storing data, providing solutions around problems faced when dealing with large amounts of data etc. There are also valid arguments for using tools that you know and understand. But these are not reasons to blatantly choose the wrong tools. We as developers need to understand the trade-offs. Understand the complexity that we introduce when choosing a database that does not fit our query model. Sometimes it is justified and sometimes not. What is important is that we make informed decisions based on our understanding of the problems we face, utilizing the knowledge we have as software engineers. If you feel you don't have the knowledge it is time to level up (or defer those decisions to those with the knowledge).

Note: If you are at the start of an application and you are unsure what your queries are going to look like, then slap on an in memory repo, write some code and once you get a feel for what you are dealing with, swap out the in memory repo with the correct type of database. You will soon find that it takes a lot less time to add a database implementation later in the dev life cycle than it is to fight with the wrong implementation.


<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
