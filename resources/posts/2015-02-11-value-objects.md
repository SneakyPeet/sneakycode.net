{:tags ("domain driven design" "ddd" "value objects" "immutability"), :title "Value Objects", :date "2015-02-11"}
-----
Continuing on the domain object theme from [my previous post](http://sneakycode.net/entity-is-for-identity/), we will be discussing Value Objects.

To recap, Domain Entities want to persist their value over time and can be uniquely identified by a single identification property.

Next we will discuss Value Objects.

So what is it?

**A Value Object is uniquely identified by the values of its properties.**

Multiple instances of a value object with the same properties, represent the same object in a particular context.

A simple example can be a point on an x-y axis.

```csharp
public class Point
{
	public Point(int x, int y){
		this.X = x;
		this.Y = y;
	}
    public int X { get; private set; }
	public int Y { get; private set; }
}

var a = new Point(2,5);
var b = new Point(2,5);
```

It is clear that `point a` and `point b` in our example are the same point because `a.X == b.X` and `a.Y == b.Y`. They represent the exact same value on our graph.

Thus identify our object by its value.

**Value objects are immutable.**

If we need to change the state of a value object, it means that the core of what the value object represents gets changed. This leaves us with the following guidelines.

* A value object will not have methods that changes it's internal state. This is why our setters on `Point` are private.
* If you need to do a calculation against a value and require a value of the same type back, you return a new instance. This means that the initial value will always keep its   integrity. Another example of this in .net is `DateTime`. The   `AddDays` method on DateTime returns a new DateTime instance.

```csharp
public Point MoveX(int delta)
{
    return new Point(this.X + delta, this.Y);
}
```

* When you are replacing a value object property on another object, you replace it instead of altering the existing object.

```csharp
public Class Graph
{
	private Point startingPoint;
	public void SetStartingPoint(Point startingPoint)
	{
		this.startingPoint = startingPoint;
	}
}
```

**A Real World Example**

Now that we have the basics down, lets look at a more realistic real world example. Suppose we have a `Member` entity with an `Address` property (with `Address` being a value object).

The biggest difference between our `Point` and `Address` examples are that we would want to store our address for retrieval later. If you read my previous post you are probably wondering if `Address` should not instead be an Entity. That depends 100% on your domain.

Lets assume we are storing our Member data in a SQL database, in the member table. We might store our address in one of the following ways.

1. Directly in the member table with a column for each property.
2. In a separate address table as a one to one mapping. Thus each member still has one address.
3. In a separate address table as a one to many mapping, assuming that the member can have more than one address.

Our member entity knows nothing about how data gets stored. It will only know that it either has one or many addresses to keep track of.

For options 1 and 2 we use a value object. Even though for option 2 we have a separate table, we still use a value object as our domain does not care about tables. The member carries the identity so the address does not have to. We can easily write a method to change the member's address.


```csharp
public Class Member
{
	public Address Address { get; private set; }
	public void ChangeAddress(Address address)
	{
		this.Address = address;
	}
}
```

Option 3 provides a bit of a challenge. If we have multiple addresses how do we know which address needs to change? In this case, even though address still fits our value object model, we need to remember that we want to have the address identity over time. This does not count for option 1 and 2 as the member carries the identity. But for option 3 our address needs to carry its identity and will be an entity instead of a value object.

**How to identify a Value Object**

The next question is how do we point out to other developers that our object is in fact a value object? Most other objects will have their job as part of their name for example `UserRepository` or `MemberFactory` etc. Our entities will inherit from IEntity. We won't be going around calling our address `AddressValueObject` though and inheriting from an interface like `IValueObject` does not make sense since we will not share common functionality. I still feel that it is useful to indicate what our object does. We can do one of the following:

* Inherit from `IValueObject`. Even though I just said that this makes no sense, it does make sense. We are calling our object what it is.
* You can write a comment. This serves the same function as `IValueObject`, although if you are writing the comment you might as well just add the inheritance.
* You can leave your object as is, and have the user of the object infer that it is a value object by noting it's immutability and usage.

What you choose should be determined by the experience of your team.

___

We are by no means done with value objects and in my next post I will be talking about more complex value objects.

Please post suggestions and critique in the comments. Discussion is how we solve problems well. Till next time.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
