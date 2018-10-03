{:tags ["testing"], :title "Better Test Documentation", :date "2016-01-25"}
-----
Let's look at some tests.

![Typical Test Folders](http://res.cloudinary.com/dltpyggxx/image/upload/v1453703519/1_dtodo7.png)

Ok, so we can assume there is some `User` and `Controller` functionality. But to really know more we have to dig deeper.

![User Tests](http://res.cloudinary.com/dltpyggxx/image/upload/v1453703517/2_jkvieq.png)

Looking at the `User` test folder, have we gained more insight?. It is only when diving into `UserTests` that we get to see what our functionality really is. And here we see the same old same old:

* A class, testing many different (and separate) things regarding `Users`.
* Long tests names all starting with `Given_Something_Then`.

I am going to make a statement: ***Well written tests serves as documentation***.

Duplication and not adhering to the Single Responsibility Principle, makes our tests hard to read, hard to maintain and we have to dig around in the code to figure out what is actually going on.

How can we make this better? By being specific! We can use namespaces/folders to extract duplication and serve as an overview of functionality. By following the Single Responsibility Principle we can advance our initial understanding by having well named files that point to single area's of functionality.

Let's look at an example.

![Content Tests](http://res.cloudinary.com/dltpyggxx/image/upload/v1453703517/3_omiqib.png)

Right of the bat we can see that we are dealing with content and more specifically content creation and publishing.

![Content Tests Folders](http://res.cloudinary.com/dltpyggxx/image/upload/v1453703518/4_tpx6fa.png)

Even more functionality is exposed by opening these folders. And when finally looking at the actual test classes, a very good overview what our code should actually be doing is gained.

![Content Tests Folders](http://res.cloudinary.com/dltpyggxx/image/upload/v1453703518/5_aqy0rw.png)

Up to this point we have not read any code. This is much better than `ContentTests` and `ContentFactoryTests` would ever be. When reading the actual code, some attention needs to be paid to the namespace and class name to get all the info regarding the tests, but this is minor to the readability gained on a folder level.


```csharp
namespace App.Tests.ContentCreation.WhenUpdatingContent
{
  [TestFixture]
  public class ThrowExceptionIf
  {

    [Test]
    [ExpectedException(typeof(LogicException))]
    public void BrowsableIdentifierNotSet()
    {

    }

    [Test]
    [ExpectedException(typeof(LogicException))]
    public void BrowsableDisplayNameNotSet()
    {

    }
  }
}

```

Personally I think this is great, but I would love to hear your comments, suggestions and improvements!

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
