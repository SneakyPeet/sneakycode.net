{:tags ("clean code" "dependency injection" "ioc" "castle windsor"), :title "Delayed Dependency Resolution the Right Way",
 :description "I have been playing around with CQRS/Event Sourcing and using lightweight, immutable command messages to trigger actions in my application. The great thing about this is that you can specify a single point of entry into your application by using a very simple interface."}
-----
I have been playing around with CQRS/Event Sourcing and using lightweight, immutable command messages to trigger actions in my application. The great thing about this is that you can specify a single point of entry into your application by using a very simple interface.

```csharp
public interface IApplication
{
    void Execute<T>(T command) where T : ICommand;
}
```
This means that every time you want to expose a new actionable feature, you just provide the client with a simple command object.

```fsharp
// this is F#
type HireEmployee = {
  EmployeeId: Guid;
  Name: String;
  HireDate: DateTime;
} with interface ICommand

```
(By the way I like implementing my command and event messages in f# because f# has structural equality and immutability by default which means I do not have to write constructor boilerplate and override equality methods.)

No more creating a bunch of Application Interfaces that your client needs to consume. You just add the appropriate command handler implementation inside of your application.

```csharp
public class HireEmployeeHandler : ICommandHandler<HireEmployee>
{
    public void Handle(HireEmployee command) {...}
}
```

**There is however a tiny issue with IoC container wireup**. Typically we are used to calling resolve on our container once at the entry point of our system. Our container will then inject all our dependencies and bob's your uncle! However can you spot the problem with our above example? By the time we need to figure out what CommandHandler to use, we have already resolved our dependencies. This means that we need to explicitly resolve the dependencies for our CommandHandler again.

Simple you say! I'll just pass my container into the code that resolves my command handler.

```csharp
class CommandDispatcher : ICommandDispatcher
{
    private readonly IContainer container;

    public CommandDispatcher(IContainer container)
    {
        this.container = container;
    }

    public void Send<T>(T command) where T : ICommand
    {
        var handler = this.container.Resolve<T>();
        handler.Handle(command);
    }
}

```

**DO NOT DO THIS!**

The clean code god's will smite you for introducing the concept of a container into your domain and doing service location. Your IoC container should sit on the outermost layer of your application and assist you in building up complex dependencies. Other than container wire up you should only be calling `container.Resolve()` in once place. If your application is small you can ask yourself *"do I even need an IoC container?"*. Remember that not having an IoC container does not mean that you are not doing Dependency Injection. Because your IoC does magic to make your life easy, it does not mean you should abuse it by building up huge dependency trees that go 10 levels deep and injecting to many dependencies.

`#rant` Let's get back to solving the problem. A better solution would be to do something like this.

```csharp
class CommandDispatcher : ICommandDispatcher
{
    private readonly ICommandHandlerFactory factory;

    public CommandDispatcher(ICommandHandlerFactory factory)
    {
        this.factory = factory;
    }

    public void Send<T>(T command) where T : ICommand
    {
        var handler = this.factory.Create(command);
        handler.Handle(command);
        this.factory.Destroy(handler); //you have to explicitly manage the life cycle of your handler
    }
}

public interface ICommandHandlerFactory
{
    ICommandHandler<T> Create<T>(T command) where T : ICommand;
    void Destroy(object handler);
}
```

How is this different? First of all you are expressing your needs without polluting your domain. If you ever decide to chuck out your IoC container for a different one, your domain would be unaffected. You would not have to change a single line of code in your domain. The only thing that you would have to do is as part of your container wireup you would need to actually implement `ICommandHandlerFactory`. Because you are doing DI, the implementation does not have to sit inside your domain, but can live outside of it alongside your container.

If you are using [Castle Windsor](https://github.com/castleproject/Windsor) you are in luck. They provide a [Typed Factory Facility](https://github.com/castleproject/Windsor/blob/master/docs/typed-factory-facility.md) which means you do not even have to implement `ICommandHandlerFactory`! You just need initialize the factory in you container wireup and bob is your uncle again!

```csharp
container.AddFacility<TypedFactoryFacility>();

container.Register(
    Component.For<ICommandHandlerFactory>()
             .AsFactory()
);

```

**Nifty!** Remember kids, Inject Responsibly!

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
