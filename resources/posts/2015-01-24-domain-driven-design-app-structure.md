{:tags ("domain driven design" "ddd" "application design"), :title "Domain Driven Design App Structure", :date "2015-01-24"}
-----
At the center of our DDD universe lies the **Domain**. It is the beating heart of our application. The life of our application flows out of the domain like blood through the body. **DRAMA!** What I am trying to get at is that the domain is really important. I mean it is right there in the name: *Domain* Driven Design. It is the place where we model our real life problem. We share this domain with the experts in the field. We use the same concepts and language that they use and we continuously engage with these experts so our model can evolve and grow as our project grows.

**Important things to remember about the Domain**

* The Domain knows only about itself. It does not depend on the other parts of the application. It is the center of the metaphorical onion.
* It is where our rules live.
* We are not afraid to refactor the domain model, and change it to fit the real life domain more closely.
* The domain provides interfaces to define persistence and integration, but it does not implement these interfaces. It knows **nothing** about how it is persisted, displayed or who uses it.
* Test coverage for the domain should be very high if not 100%. It lends itself well to TDD and because of its isolation, change is easy.

But the domain does not stand alone.

**Persistence**

Oh look, a database! As stated previously the domain will provide the persistence interface, but the persistence layer will be responsible for the implementation. The domain does not care if its data is stored in MySQL, MongoDB, Text Files or just sent into the ether. The repository pattern lends itself well to DDD and to change how we persist should theoretically be as easy as replacing a DLL. Not so trivial in practice though, but we will get to that.

**Integration**

This is where the application talks to the outside world. We call web services and send emails from the integration layer.

**Application**

The application layer is like a giant blanket covering the domain, persistence and integration layers. It is where our use cases live. It orchestrates the interactions between persistence, integration, users and the domain. Anyone who wants to use the application will do so through the application layer. They will know nothing about the domain, persistence or integration. How does this look in the real world? Say I have an MVC Web App. Our Application will take the role of the Model.

To visualise what I am trying to say I made a little gif (or *jif* for the hipsters)

![Overview of a Domain Driven Design Application](http://res.cloudinary.com/dltpyggxx/image/upload/v1422105983/output_FuKgD4_es43ww.gif)

So there you have it. My work here is done...

No no no not really! We still need to talk about how to practically do all of the above. But for now I am tired of typing :) See you soon!

___

Part two of this post can be found [here](http://sneakycode.net/domain-driven-design-app-structure-part-2/).

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
