{:tags ("Domain Driven Design" "DDD"), :title "Introduction To Practical Domain Driven Design", :date "2015-01-19"}
-----
**Domain Driven Design** (DDD) is a very theoretical field ([Go Read The Big Blue Book](http://www.amazon.com/exec/obidos/ASIN/0321125215/domainlanguag-20)). Even though all the concepts of DDD (like entities and repositories) are easily implemented, few examples of these exist under the DDD banner. The reason for this is that the focus of DDD is just that, it highlights the Domain. Domain driven design differentiates itself from concepts like MVC and SOA, by not providing a blue print for how code will be structured. Instead it focusses primarily on the problem domain. A well implemented DDD application will highlight domain and not architecture. 

>But what do you mean? Am I not building an MVC application? 

Take a look at the two folder structures below. 
<div style="text-align: center;"><img src="http://res.cloudinary.com/dltpyggxx/image/upload/v1421694756/FolderStructure_e6qhri.png" alt="Architecture vs. Domain file structures" style="max-width: 400px; margin-bottom:1em"/></div>

[Uncle Bob Martin](https://twitter.com/unclebobmartin) would say the one on the left screams `Model View Controller` and the one on the right `Business Administration`. The one on the right is what DDD tries to get at. **The business becomes the core of the application.** This helps us solve difficult business problems without the "noise" of software engineering patterns and practices. This domain will ultimately share the same language that the experts in the field use, thus improving communication between developer and business, and ultimately giving you a happy customer.

Another great side effect is that your concerns are clearly separated. **The core of the application will have no clue about persistence or UI.** This frees us up to clearly define the functionality of the app, and leaves the technology decisions for later. 

> So I can prevent scenarios where an app is built on a relational database just to realize later that a document store would have been more appropriate!

This is a usually a total mind shift for developers as the first thing we think about is the database.
No longer will we be saying:" *This app should be a SPA with a web api backend, EntityFramework as ORM and SQL server as data store*." You would rather say:" *This will be a scheduling app that helps the business manage employee time, thus increasing productivity and ultimately maximising profit*".

> But we have already decided that we will use MySQL as this is company policy.

This is also fine. Your domain does not care about the tech. The great thing about this separation of concerns is that DDD lends itself to testing. There are very well defined seams in your application and because your domain is isolated at the center of the app **there is no reason not to have 100% code coverage** of your business rules.

###So in conclusion 
Domain Driven Design provides us with `Clear`, `Maintainable` and `Testable` code that speaks the `language` of the business.

>But Pieter, all this is just theory and good ideas, but how do I actually make this happen?

For the next few weeks I will be taking the above theory and putting it to practice. I will be showing you how to make your applications scream domain, but I will also show you the practical side, the software side :) I will be using .net and c#, but there will be no reason why you will not be able to take these principles to other technologies.

Let us learn together and be awesome!

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>