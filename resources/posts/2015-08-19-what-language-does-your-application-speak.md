{:tags ("clean code" "Domain Driven Design" "DDD" "ubiquitous language"), :title "What Language Does Your Application Speak", :date "2015-08-19"}
-----
If you are doing Domain Driven Design (or any application with business logic for that matter), you should be talking in two languages. 

The first is the **ubiquitous language**. Yes, yes I know big word. But if you have to choose one thing that makes DDD what it is, it is the ubiquitous language. Without it you are simply not doing DDD.

Well what is it? In short it is the language that you and your team, and your business owner and you domain experts and clients develop together to describe the domain that you are working with. When you talk the ubiquitous language, everyone on your team knows what you are talking about. When you use it in your code, even non developers sharing your ubiquitous language will be able to look at your code and know what the context is. Your code becomes self documenting and the language outlives developers as the context is kept within the language the entire team speaks. It is the basis of the model that you develop to describe the domain. It tells you **What** your application does. Are you writing methods like update, create and delete? You are not talking the ubiquitous language. Do you have classes called manager, service or processor? You are not talking the ubiquitous language. Are you having trouble naming things? Maybe the language that you have developed is not a good representation of the domain. Maybe you have to get back together with your domain experts and talk it out. 

The second is **architecture language**. This is the language of the developer. This is your controllers, your factories, your strategies. It is the language you use to describe how your applications fits together. It tells you **How** your applications does it's job. 

The goal of these two languages is to promote maintainability and help you solve the customers problem well.

These languages should not be mixed, however they will meet at certain points in your application that you should define well. Here is a good example of such a boundary

```language-csharp
public class ApplyForLeaveCommandHandler : ICommandHandler<ApplyForLeave>
{
    private readonly IRepository repo;
    public ApplyForLeaveCommandHandler(IRepository repo)
    {
        this.repo = repo;
    }

    public void Handle(ApplyForLeave cmd)
    {
        var employee = this.repo.GetById<Employee>(cmd.EmployeeId);
        var leaveEntry = employee.ApplyForLeave(cmd.LeaveId, cmd.StartDate, cmd.EndDate);
        this.repo.Add(leaveEntry);
    }
}
```

Looking at this class and the words `CommandHandler` and `Repository`, you might see the resemblance of the command pattern or even event sourcing. You see that it is handling application flow. This class is clearly talking architecture.

`ApplyForLeave` and `employee.ApplyForLeave` is your ubiquitous language and to understand your business logic, you delve into these classes and methods. When your rules change, your app flow does not. When your architecture changes, your rules are unaffected. Your architecture knows how to talk the ubiquitous language, but has no idea how your business logic is actually implemented. Your domain should only talk ubiquitous language, and should mostly be totally unaware of architecture language.

Does your applications need to learn a new language?

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>