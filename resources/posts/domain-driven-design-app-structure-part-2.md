{:tags ("clean code" "Domain Driven Design" "DDD" "Application Design"), :title "Domain Driven Design App Structure part 2", :date "2015-01-27"}
-----
In my [previous post](http://sneakycode.net/domain-driven-design-app-structure/), I gave an overview of what an implementation of a Domain Driven Design Application might look like.

I will be running a C# project in parallel with my Practical Domain Driven Design Articles. The plan is to eventually provide a full project template that can be used either as a DDD reference project or as a starting point for a new application. You can find the **SimpleDDD** project on [GitHub](https://github.com/SneakyPeet/SimpleDDD/blob/master/README.md).
___
**Kicking off SimpleDDD**

If you have not read my **[previous post](http://sneakycode.net/domain-driven-design-app-structure/)** go do that first as this will give you some context.

Opening the project you will find an application folder with 4 projects inside. I have named these projects a bit different than in my previous post. Exact names for these layers are not important, they should just be named clearly.

**SimpleDDD.Domain**

* This is our Domain/Model layer
* It can also be called Core if you like
* It depends on no other projects in our solution

**SimpleDDD.Data**

* This is our Persistence Layer
* This layer will probably get renamed later to indicate the technology used. For example *SimpleDDD.Data.EntityFramework*
* It depends on SimpleDDD.Domain

**SimpleDDD.Integration**

* Our integration layer (duh)
* Depends on SimpleDDD.Domain

**SimpleDDD.Api**

* This is our Application layer
* I have gone back and forth a few times on what this should be called. It is fine to call it Application or App or even AppBoundary. If you have a really big system it can even be named after your Bounded Context. I find that Api provides a nice naming convention. I prefer something like UserApi over UserApplicationService.
* It depends on all three other projects
* It is the project that will get used by consumers

You will see that I have not added any consuming projects like a console app. We will do so later on when we talk about boundaries, DI and object lifetimes.

I have also cleared out all unused references added by visual studio. This might seem a bit redundant but we want to maintain a clean, fast compiling solution, thus we will stick to good principles from the start.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>