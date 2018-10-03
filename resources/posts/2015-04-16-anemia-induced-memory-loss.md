{:tags ("domain driven design" "ddd" "aggregates" "anemic domain" "rich domain"), :title "Anemia induced memory loss", :date "2015-04-16"}
-----
I have been reading [Implementing Domain Driven Design](https://vaughnvernon.co/?page_id=168) by Vaughn Vernon. In it he mentions the concept of Anemia induced memory loss. Say what? Basically an anemic domain is a domain that lacks information about itself. All of its information lives in other places(like services). For you to learn about the domain, you have to know where to go look. This usually also requires some context about the decisions that the developer that wrote that piece of functionality made. As time goes by, this context gets lost, meaning all those details you had to know is either hard to find or completely lost. There is no one place to find the context thus you have to look everywhere. This is what anemia induced memory loss means.

DDD combats this by focusing on a rich domain. A domain that knows everything about itself. The domain no longer asks the questions, it provides the answers.  If you need to know something about something, you go to that something and find it out. To achieve this however, is not an easy skill to learn. We need to learn to model a domain that makes sense to both developers and domain experts. Ubiquitous language and aggregates are the key. However it takes time and practice and guidance to really become a modeling master.

Luckily we have a new initiative at our company called [Entelect](http://www.entelect.co.za/) Dojo, and my buddy [Eugene](https://twitter.com/eldevilliers) and myself gave a talk on domain driven design and how to start thinking "rich domain". We had a nice comparison of a domain implemented using [transaction script](http://martinfowler.com/eaaCatalog/transactionScript.html)(aka anemic domain) and a rich domain model. The Anemia induced memory loss is blatantly obvious so I thought I would run through the two examples. This will also give you a sense of how to start thinking rich domain.

___

###First we need some requirements.

* Employees can capture Annual and Study leave.
* There are various rules around capturing leave.

___

###Anemic Domain
Typically we will have a `LeaveEntry` poco that we use everywhere. We might also have a `LeaveService` that is responsible for control flow (mapping, validation, persistence) and business rules.

```csharp
public enum LeaveType
{
    Annual,
    Study
}

public class LeaveEntry
{
    public LeaveEntry()
    {
        this.Approvers = new List<Employee>();
    }

    public int EmployeeId { get; set; }

    public DateTime StartDate { get; set; }

    public DateTime EndDate { get; set; }

    public LeaveType LeaveType { get; set; }

    public LeaveStatus LeaveStatus { get; set; }

    public List<Employee> Approvers { get; private set; }

    public Employee CurrentApprover { get; set; }
}

```

Lets assume that the business called and added the following requirements.

* There is a new Sick Leave Type
* Sick leave can only be assigned if a sick note is attached

We might change our `LeaveEntry` as follows


```csharp
public enum LeaveType
{
    Annual,
    Study,
    Sick //Change
}

public class LeaveEntry
{
    public LeaveEntry()
    {
        this.Approvers = new List<Employee>();
    }

    public int EmployeeId { get; set; }

    public DateTime StartDate { get; set; }

    public DateTime EndDate { get; set; }

    public LeaveType LeaveType { get; set; }

    public LeaveStatus LeaveStatus { get; set; }

    public List<Employee> Approvers { get; private set; }

    public Employee CurrentApprover { get; set; }

    public byte[] Document { get; set; } // Change
}

```

Can you spot the Anemia induced memory loss? Let's say a new team member joins and the first thing he sees is the `LeaveEntry` class. What is he going to think? All leave entries requires a document? He will be wrong ofcourse, but there is nothing here that points at that rule. If he does not know about the rule, he will not know to go and look for it elsewhere. This is the trap that an anemic domain sets.

___

In a rich domain we would stick with the `LeaveEntry` as our aggregateroot. But we would move all the business rules out of our `LeaveService` into our `LeaveEntry`. We would then also decide to either make `LeaveEntry` a base class and have our `LeaveTypes` inherit from it, or compose our `LeaveEntry` with a `LeaveType` (you do this typically if the type can change). Our document will only live on our `SickLeaveType` and we will also find the validation on that type. Thus the rules and intent around sick leave is clear and complete and in one place.

Please jump over to [github](https://github.com/entelect/Dojo_DomainDrivenDesign) and have a look at this example with more rules and coded examples for both anemic and rich domain. Note that how any change in the rich domain models, have no impact on control flow. This is great because it means we can only write tests for our new classes. Also note how easy it is to test our rules without having to mock anything. This is the beauty of a rich domain.

You might notice that the Rich Domain is a bit more complex and not really less code. Modeling and modeling well is hard. But the reward can be massive.

That said there is nothing wrong with transaction script and it has its place, just like there are problems to which DDD is not the right solution. But if you tend to have more than a few rules, DDD starts to shine.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
