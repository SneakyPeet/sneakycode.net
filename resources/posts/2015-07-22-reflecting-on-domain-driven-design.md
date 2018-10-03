{:tags ("domain driven design" "ddd"), :title "Reflecting on Domain Driven Design", :date "2015-07-22"}
-----
When I started blogging about DDD, I mentioned that my plan was to build up a [DDD project template](https://github.com/SneakyPeet/SimpleDDD). I wanted to do this because there are very few examples out there on how to do DDD.

Looking at that Github project you will see that almost nothing has happened since its creation.

> **Why?**

The reason why is the same reason why there are not many DDD project examples out there. DDD is not about application structure. It is not about application services, repositories, entities and value objects. Although these are important they only exist to support the application and help keep it maintainable. You can do domain driven design without using any of these.

DDD is about **Ubiquitous Language**.

It's about **Bounded Context**.

It is having your software talk the language of your domain experts. It is collaborative modeling with the entire team.

> **How do you make a project template for this?**

You don't.

But what about repositories and application services and all these things? Surely they are very important. Using these things does not mean you are doing DDD, even though they are described in the [Domain Driven Design bible](http://dddcommunity.org/book/evans_2003/). If you talk to Eric Evans today he will tell you the same. Yes I can put a bunch of these into a project template, but that is blatantly over engineering. A software product is not a bunch of layers and software concepts. It is about features that makes the users life better. These concepts exist only to support these features and should only be implemented when really required and to promote maintainability.

> **What does this mean?**

DDD is hard. It is not something you can read in a book and then implement like a design pattern. It is something to be practiced, preferably with people that have done it before. You have to do it, you have to fight with it, you have to fail and succeed. Only then it will become part of your software engineering tool belt.

This does not mean we have to stop discussing DDD. No! It should serve as motivation for discussion. In future post I will most definitely continue tackling the practical assets of DDD and I am looking forward to having these discussions with you.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
