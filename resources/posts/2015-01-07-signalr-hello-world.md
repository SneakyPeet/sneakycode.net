{:tags ("csharp" "signalR" "javascript" "js" "asp.net"), :title "SignalR Hello World (That is not a chat app)", :date "2015-01-07"}
-----
I have been wanting to try out SignalR for a while now and finally came up with an idea that is simple enough to start with (that is not a chat application).

I am not going to go into the details of how SignalR works, just show you the code to get started. If you have never heard about SignalR, read the first two paragraphs [here](http://signalr.net/).

Our app will do the following

* The User navigates to a page on their desktop. We will display a QR code on the page
* The User will scan the QR code with their smart phone and get redirected to a page on their mobile browser
* The User will then see live orientation data of their phone on their desktop

To keep the post short I am only going to show you the code relevant to SignalR. You can checkout the [demo](http://sneakysignal.azurewebsites.net/) and the [source code](https://github.com/SneakyPeet/SneakySignal) for the full details.

**Let's Get Started**

Open up Visual Studio and create a new *ASP.NET MVC 5* application.

Grab the following library via nuget

* Microsoft ASP.NET Web SignalR

**On The Server**

The first thing we need to do is hook-up SignalR to our app start-up. We do this by creating a file and adding the following code. When the app starts up the Configuration function will automatically get called. **Note: SneakySignal is the name of the project and has nothing to do with SignalR.* 

<pre><code class="language-csharp">using Microsoft.Owin;
using Owin;
[assembly: OwinStartup(typeof(SneakySignal.Startup))]
namespace SneakySignal
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            app.MapSignalR();
        }
    }
}</code></pre>

Next we need to add the code that will handle the communication between server and client. For now all you need to know is that SignalR uses the concept of hubs. You can read more about it [here](http://www.asp.net/signalr/overview/guide-to-the-api). 

Create the MotionHub.cs class that inherits from Hub. 

<pre><code class="language-csharp">using Microsoft.AspNet.SignalR;
namespace SneakySignal
{
    public class MotionHub : Hub
    {
        //Desktop and phone uses this to get their connection id
        public string GetConnectionId()
        {
            return Context.ConnectionId;
        }
		
        //Called by the phone
        //Tells the desktop that the phone wants to connect
        public void ClientConnected(string connectionId)
        {
            var clientId = Context.ConnectionId;
            Clients.Client(connectionId).clientConnected(clientId);
        }
        
        //Called by the desktop
        //Tells the phone that it is connected and can start sending data
        public void StartExecution(string connectionId)
        {
            Clients.Client(connectionId).startExecution();
        }
        
        //Called by the phone
        //Tells the desktop that the orientation has changed
        public void OrientationChanged(string connectionId, OrientationData orientationData)
        {
            Clients.Client(connectionId).orientationChanged(orientationData);
        }
    }
  
    public class OrientationData
	{
    	public decimal Alpha { get; set; }
    	public decimal Beta { get; set; }
    	public decimal Gamma { get; set; }
	}
}</code></pre>

As you can see we only have to write the business logic specific to our app as SignalR handles all the connection management.

Last but not least add the following lines in your `_layout.cshtml` file.

<pre><code class="language-bash">/Scripts/jquery.signalR-2.1.2.min.js
/signalr/js</code></pre>

`/signalr/js` is a convention used by SignalR. On build our MobileHub will generate some javascript that will be found on this route.

Now for the client side implementations.

**Desktop Client**
<pre><code class="language-javascript">var hub = $.connection.motionHub;
//register mobile and tell it to start executing
hub.client.ClientConnected = function (clientId) {
	hub.server.startExecution(clientConnectionId);
};
//update orientation
hub.client.orientationChanged = function (orientation) {
        
};

//connect to server
$.connection.hub.start().done(function () {
	hub.server.getConnectionId().done(function (desktopConnectionId) {
		//show QR code with url containing desktop connection id
	});
})</code></pre>

The first thing you will see is that we get our specific hub from `$.connection.motionHub`. We then subscribe to the clientConnected and orientationChanged methods that we specified in our `MotionHub.cs` file. 

The last thing is to connect to the server using $`.connection.hub.start()`. When we are connected to the server we ask it to provide us with our connection id.

**Mobile Client**
<pre><code class="language-javascript">var hub = $.connection.motionHub;

hub.client.StartExecution = function () {
  window.addEventListener("deviceorientation", function(orientation){
  	  hub.server.orientationChanged(desktopConnectionId, orientation);
  });
};
//connect to server
$.connection.hub.start().done(function () {
	hub.server.clientConnected(desktopConnectionId).done();
});</code></pre>

The javascript on the mobile side is very similar. First we subscribe to `startExecution`. In this method we add an event listener that listens for the `deviceorientation` event. On an event we call `orientationChanged` on the server with the new orientation.

All that is left is to connect to the server and tell the relevant desktop that we want to connect to it.

**note. I left out a lot of logic like event throttling etc. to keep it concise.*

To summarize what is actually happening have a look at the image below.

![SignalR hello world program flow](https://farm8.staticflickr.com/7545/16034625040_89802f3c7c_o.png)

**And thats it**

I was impressed by how simple it is to get something awesome done so easily. Obviously there is a lot more to SignalR and what you can do with it, but for a hello world this really turned out great. Jump over to the [demo](http://sneakysignal.azurewebsites.net/) and have a look.
___
*This post was first published on [blog.entelect.co.za](http://blog.entelect.co.za/home)*

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
