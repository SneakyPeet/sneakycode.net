{:tags ("domain driven design" "ddd" "calue objects" "immutability"), :title "Make Those Value Objects Work For You", :date "2015-02-18"}
-----
We can have value objects be more than just information carriers. We can have them do some work for us.

**A Scenario**

Given a contract and some time sheet data for an employee we need to calculate invoice lines for billing a client. 

We can use an object called an InvoiceLineCalculator to calculate the resulting invoice lines. Even though an object like this feels different from the [ValueObjects we talked about previously](http://sneakycode.net/value-objects/), I am going to argue that it can in fact be a ValueObject if we follow these rules:

* It can be identified by the value of its outputs
* It is immutable

The calculated invoice lines should always be the same given the same calculator input data, thus satisfying the first rule. 

We satisfy immutability by enforcing two things: 

* After calculation we should not be able to change the output values of the calculator. We achieve this by using private setters and concepts like IReadonlyList<T>.
* We should also not keep references to input data. Even though we might pass complex objects to the calculator via its constructor, we should not keep references to these objects. Firstly we might not want to keep these objects around as long as our calculator. Secondly if we keep references around we run the risk of depending on these references in our output, thus if another object changes values on our reference object, it might cause changes to our ValueObject output, breaking immutability.

**But can't we just use a Domain Service?**

Short answer, yes! Long answer, it depends. For the simple example above it might make sense to just have a domain service with a calculate method that returns the invoice lines. But let's say there are multiple contract types, each with its own invoicing rules. Furthermore we might expect to be provided with other data regarding the calculation. This would mean our service has to know the calculations for all the different contract types. The service will soon become cluttered with details of each calculation. 

Below is an example of an InvoiceLineCalculator as a ValueObject.

```language-csharp
public class WeeklyRetainerLineCalculator : IInvoiceLineCalculator
{
    public bool ShouldInvoice { get; private set; }
    public IReadOnlyList<InvoiceLine> InvoiceLines { get; private set; }

	public WeeklyRetainerLineCalculator(Retainer retainer, List<Timesheet> timesheets )
    {
        Calculate(retainer, timesheets);
    }

    private void Calculate(Retainer retainer, List<Timesheet> timesheets)
    {
        //calculate invoice lines
    }
}
```

Having a single object per contract type proves to be cleaner. Note the immutability. Note the single responsibility. We will typically use a factory to construct our different calculator types based on input values. This means that when we need to add another contract/calculator type, we do not have to edit any control flow. We just add the decision line in our factory and implement the new calculator. Polymorph much?

Another great side effect of the above code is that it is easily testable and if needed the class can be removed with very little side effects. It is of course very important to name these classes well, to make the intent clear.

**But Why**

The most important thing to take away from this post is that immutability is your friend. When you set out to write immutable objects, intent stays clear, side-effects are limited and ultimately bugs are avoided.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
 
