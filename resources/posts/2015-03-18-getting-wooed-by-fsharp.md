{:tags ("domain driven design" "ddd" "fsharp"), :title "Getting wooed by F#", :date "2015-03-18"}
-----
One of my goals this year is to learn a new programming language. This language should use a programming style different than what I am familiar with (currently C# and Javascript). As I am settled in the .net space I decided to go with F#. I am only about a week in, but man I have to say I am excited. So excited that I could not hold it in any longer and decided to write about what is getting me excited. I have to stress that my F# skills are equivalent to a baby sliding around on its stomach learning to crawl. Regardless, what I have seen so far is awesome.

I am currently working through all the content on [fsharpforfunandprofit](http://fsharpforfunandprofit.com/) by [Scott Wlaschin](https://twitter.com/ScottWlaschin), and I am borrowing the below example come from there.

___

So my first real surprise is how well F# lends itself to Modeling a Domain. I am going to show you this by using a card game as an example.

Here is what it might look like in c#.

```language-csharp
public enum Suit
{
    Club,
    Diamond,
    Spade,
    Heart
}

public enum Rank
{
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King,
    Ace
}

public class Card
{
    public Card(Suit suit, Rank rank)
    {
        this.Suit = suit;
        this.Rank = rank;
    }

    public Suit Suit { get; private set; }
    public Rank Rank { get; private set; }
}

public class Hand
{
    public Hand(List<Card> cards)
    {
        this.Cards = cards ?? new List<Card>();
    }

    public List<Card> Cards { get; private set; }
}

public class Deck
{
    public Deck(List<Card> cards)
    {
        this.Cards = cards ?? new List<Card>();
    }

    public List<Card> Cards { get; private set; }
}

public class Player
{
    public Player(string name, Hand hand)
    {
        this.Name = name;
        this.Hand = hand;
    }

    public string Name { get; private set; }
    public Hand Hand { get; private set; }
}

public class Game
{
    public Game(Deck deck, List<Player> players)
    {
        this.Deck = deck;
        this.Players = players ?? new List<Player>();
    }

    public Deck Deck { get; private set; }
    public List<Player> Players { get; private set; }
}
```

Pretty straight forward stuff. I showed this to my wife that knows nothing about code and she had no idea what was going on.

Then I showed her this.

```language-csharp
module CardGame = 
    
    type Suit = Club | Diamond | Spade | Heart

    type Rank = Two | Three | Four | Five | Six | Seven | Eight | Nine | Ten | Jack | Queen | King | Ace

    type Card = Suit * Rank

    type Hand = Card list

    type Deck = Card list

    type Player = { Name:string; Hand:Hand}

    type Game = {Deck:Deck; Players: Player list}

    type Deal = Deck -> (Deck*Card)
    
    type PickupCard = (Hand*Card) -> Hand
```

The above is not pseudo code. It valid f# that compiles. First note the total number of lines. 80 vs 19 like what! I showed this to my wife and even though I still had to explain some of it, she got the gist just by looking at it. This means that a domain expert should fairly easily be able to confirm a domain model correctness, without understanding f#. 

I know there is no behavior in the above example, but the conciseness and lack of clutter really got my attention. When the f# guys where talking about writing less code, they where not kidding. 

What is not apparent from the example is that you get a lot of things for free

* Types are immutable by default. GoGo [Value Types](http://sneakycode.net/value-objects/). Mutability can be chosen explicitly, but the immutability forces us to transform data into new data, rather than changing it. This prevents bugs and side effects.
* Types are equatable by default. We get comparison, max, min and ordering for free. Again this is great for Value Types.

```language-csharp
let highcard = (Heart, Ace)
let lowcard = (Club, Six)
highcard < lowcard //false
let randomcard = (Club,Six)
lowcard = randomcard //true
```

* Null reference exceptions are excluded from the above code as non of these types can be null. If you need nullable functionality, you add it explicitly.
* New primitive types are basically free. We can easily make a string type that has to be less than 40 characters and then use that where we have a less than 40 characters business rule. Although this is possible in c#, it is a lot more code and hassle and we end up not doing it.
* Less code means less bugs.

I am going to stop here before I re-write [Scott's](https://twitter.com/ScottWlaschin) website. I have not even began to scratch the surface, but if what I have uncovered so far is an indication of what is to come, I am definitely going to become an F# developer. 

I also found a [Case Study for Type-safe Domain Modeling in F#](http://deliberate-software.com/pattern-matching-case-study/) on reddit.

and this [Does the Language You Use Make a Difference (revisited)?](http://simontylercousins.net/does-the-language-you-use-make-a-difference-revisited/)

If you want to find out more have a look at the [Why use F#](http://fsharpforfunandprofit.com/why-use-fsharp/) series.

___
**Update**

To give some credit back to c# I did the following

* Put all the enums on one line, even though this is not convention
* Auto initialized the lists (available in C# 6)

This brought the line count for c# down to 55 vs 19 for f#. I then figured you can write everything on one line and that a word/character count might be a better metric. C# had 160 words and 1063 characters. F# 82 Words and 457 Characters. There is still a significant difference. 

I also feel the need to say that I still love c# and I think it is a great language. This post was less about c# being long winded and more about f# being really compact.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
