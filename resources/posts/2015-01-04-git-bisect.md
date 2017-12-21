{:tags ("git" "source control"), :title "Git Goodies That You Might Not Know About: git bisect", :date "2015-01-04"}
-----
I recently found myself with an obscure bug that was introduced into the codebase somewhere in the last 50 commits. To make matters worse the bug was not apparent and was not easily seen when looking at the code. All I knew was when last that feature worked. I ended going through each commit until I found the breaking commit. If only I knew about git bisect.

Git bisect is a debugging tool built into git. It helps you perform a binary search through your commit history for a broken commit. It is great because it does all the heavy lifting for you as you only need to tell it if a commit is good or bad. Git will then select a commit in between the current commit and either the good/bad commit and check that out for you. You then test that commit and repeat the process until git finds you the bad commit.

Example time. Let’s say you know your feature worked in v1.0, but now it is broken.

To the command line!
<pre><code class="language-bash">$ git bisect start
$ git bisect bad</code></pre>

This tells git to start the bisect process and that the commit you are on is a bad one. Next we need to tell git what the good commit was.

<pre><code class="language-bash">$ git bisect good v1.0</code></pre>
    
Git will then automatically checkout a commit somewhere in the middle and give you the amount of revisions to test after the current one. You can then just test the feature. Let’s pretend the feature is still broken. You tell git that this commit is bad.

<pre><code class="language-bash">$ git bisect bad</code></pre>

Git now checks out a commit between this bad boy and v1.0. We pretend the new commit is good so we run

<pre><code class="language-bash">$ git bisect good</code></pre>

And so on and so forth. Git will continue the process until there is only the bad commit left. At this point git will provide you with a message like
    
<pre><code class="language-bash">40173afa01f57833efec7bcaab82b83b184cd5c8 is the first bad commit
commit 40173afa01f57833efec7bcaab82b83b184cd5c8
Author: Jane Doe <jane@somewhere.co.za>
Date:   Tue Sep 9 17:22:31 2014 +0200</code></pre>

You can now just check the changes and start giving fines and blames to the culprit.

To finish the process and return to where you started just run

<pre><code class="language-bash">$ git bisect reset</code></pre>

Important. It makes a lot of sense to do smaller commits more frequently. It is a lot easier to find a breaking change in 2 or 3 files than it is in a 100. Make branches commit often.

---

More info on git bisect [here](http://git-scm.com/docs/git-bisect)
___
*This post was first published on [blog.entelect.co.za](http://blog.entelect.co.za/home)*

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>