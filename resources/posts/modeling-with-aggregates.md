{:tags ("Domain Driven Design" "Aggregates"), :title "Modeling with Aggregates", :date "2015-03-12"}
-----
Modeling is at the heart of Domain Driven Design. It is also the reason that there are not many DDD examples out there. It is easy to give an example of an architecture like MVC as the different parts are very logical. But with DDD each solution is so tightly coupled to the domain and the problem that needs to be solved that simple examples rarely captures the essence of what a successful model might be. Learning how to model cannot just be taught in a blog post, or even a few. It is a learned skill that takes time, some might say years, to learn. You need to get your hands dirty. That said there are tools that you use with you modeling. Entities and value objects form part of these tools, but probably the most important one is the aggregate.

**So what is an Aggregate?**

Typical flow through the application layer ([or api](http://sneakycode.net/domain-driven-design-app-structure/)) might look something like this: 

![Typical DDD action flow](http://res.cloudinary.com/dltpyggxx/image/upload/v1426189853/Capture1_pobevv.png)

In some cases the *Does work on the domain* step might look as follows:

![Typical DDD aggregate flow](http://res.cloudinary.com/dltpyggxx/image/upload/v1426189853/Capture2_wsiypi.png)

What you need to understand is that aggregates are self-contained units that we work with to avoid messing up our code with a tangle of interconnected objects. Just like the application layer contains the application and provides access to it via clearly defined entry points, the aggregate contains all information that it needs and the methods to do the required work. Our entire domain centers on these aggregates. If we fail at aggregates, we fail at DDD.

**So how are they made and used?**

An aggregate is typically made as follows

* The domain makes a new instance. Typically using a factory.
* The application layer asks a repository to build up an instance from existing data.

and used as follows

* Directly by the application layer.
* By some domain service when we need to work on multiple aggregates.

**What does an Aggregate look like**

An aggregate is made up of two main parts namely a boundary and a root.

![Domain Driven Design Aggregate](http://res.cloudinary.com/dltpyggxx/image/upload/v1426190040/AR_gyjejd.png)
The boundary(B on the image) defines the contents of the aggregate. It is also the barrier between the aggregate contents and the rest of the application. Nothing outside the boundary can keep a reference to anything inside the boundary. 

The Aggregate Root (AR on the image) provides access to the aggregate contents. It is the only object in the aggregate that objects outside the aggregate may hold a reference to. The aggregate root in turn holds references to the aggregate contents. Although I said it provides access to the contents, it is not direct access. We will not ask the root for an object and then do work on the object. We would ask the aggregate to do the work on the object for us. In-fact we might be oblivious of the objects existence. The contents of an aggregate is made up of our basic DDD building blocks. Entities, Value Objects and other Aggregates. 

**Can Haz Example?**

The best way to give an example of an aggregate is by using the invoice analogy. 

Our example aggregate is made up of the following:

* An Invoice
* A List of Invoice Lines
* A Customer
* A Billing Address

It is fairly straight forward to identify the root. It is the Invoice. The invoice will keep references to it's invoice lines and to the customer the invoice is for. The application in turn will only keep a reference to the invoice. If we want to add or remove invoice lines, we ask the invoice to do so. If we want to update the billing address we ask the invoice. The invoice in turn asks the customer to update it's billing address. In this case the customer is also an aggregate root. The invoice, invoice lines and customer will all typically be Entities. 

**So what is the point?**

To build a rich domain. The goal is to clearly define logical self-contained pieces of work. By clearly defining the boundaries and roots, we directly limit cross cutting dependencies and thus prevent a ball of spaghetti. We force ourselves to get all the information we need upfront, and then do the work we want. We force the lowest level objects to be responsible for their behavior. This makes it easy for us to find behavior as we know where to look for it. This helps us to limit duplicate code. It also makes testing a breeze as we move all our behavior to the center of our application. There are no outward dependencies so mocking is rarely needed and it is easy to maintain close to a 100% code coverage.

___
   
The invoice model is straight forward, but you will probably find that most aggregates are not as easily defined. We should model our aggregates in a way that clearly represents the problem domain and we should use the ubiquitous language shared by developers and domain experts to make their purpose clear.  But this can be really really hard, but is really really worth it. 

Practice really makes perfect. If you can find someone that knows how to do model properly and is willing to teach you, it is even better.

More on aggregates soon ;)

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>