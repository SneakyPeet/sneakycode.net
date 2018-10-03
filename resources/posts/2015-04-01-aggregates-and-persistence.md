{:tags ["domain driven design", "ddd", "testing", "aggregates"], :title "Aggregates and Persistence", :date "2015-04-01"}
-----
In a previous post I gave an overview of [aggregates](http://sneakycode.net/modeling-with-aggregates/). In that post I said that aggregates are self-contained units that we work with to avoid messing up our code. Aggregates help us build a rich domain. This is a domain that knows about itself. When you want to know something, you ask the domain. When you want something done, you ask the domain. For the domain to be able to answer your questions and perform your tasks, it needs all the information. To do this we construct our aggregates with all the data that they need. Ideally we would like our control flow to look something like this:

```csharp
apiLayer.DoSomeWork()
{
  domain = dataLayer.getData()
  domain.doTheWork()
  dataLayer.saveData()
}
```

To achieve this we need to limit our trips to the persistence layer. This means we load all the data that we need upfront, and then construct our aggregate. To take it one step further we can ask our persistence layer to provide us with our constructed aggregate. This is where dependence on the domain become apparent. For the persistence layer to provide us with a domain object, it needs to know about the domain. When thinking about layered architecture we are used to the persistence layer being at the center of the onion. But moving the domain into the center means that we can model our domain without ever caring about persistence. This reduces dependencies in the domain considerably, meaning that our domain is void of program flow clutter, clearly defined and very testable. The problem we need to solve becomes the focus of our application.

Domain Driven Design typically uses the repository pattern. Our repositories are responsible to abstract the persistence details away from our domain. All that the domain will know is that it can get data in the form of aggregates and persist data in the form of aggregates. To do this we do two things

* We declare the interface for our repositories in the domain layer. This might seem counter intuitive, but having our interface live in the domain layer means that we completely decouple our persistence from our app. Thus if we are forced to change the persistence implementation, the only code we need to write is our new implementation and update our dependency injection. Our application layer will be ignorant of this change (except for DI code) because as far as it is concerned it uses the contracts defined in the domain. Well how often do we actually change out our persistence? Not that often, but the principle extends beyond just persistence. We use it for other infrastructure code as well. I currently have colleagues who need to change from LINQ to SQL to EntityFramework, and if they had this in place the change would have been a lot less hectic
* The second thing we do is return Aggregates (and not entities) from our persistence layer. This is to force us to load all we need to answer the questions we have. This does mean that the mapping from our persistence to our domain will be more complex, but moving this code out of our application and domain layers means that intent becomes clear and that we are moving toward single responsibility. Domain handles rules, Application handles flow and Persistence handles data mapping.

The above concepts might be weird at first and it is something that needs to be wrestled with and tried before the usefulness can be experienced. It is also not always straight forward to implement the persistence layer, but the gains are worth it.

The next big question to ask is whether to use generic repos or not, but this is a topic for another post.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
