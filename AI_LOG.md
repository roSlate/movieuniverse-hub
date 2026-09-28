### Pushing back against documentation style:

```
Don't you think this document will become way too dense? I'm genuinely asking, it's the impression I'm getting, but 
push back if you disagree.
```

### Also pushing back against the amount of information exposure:

```
Again, it's fine that you thought of all of this, but I dislike this approach. It's too information dense, I have a 
lot of trouble keeping up like this. I'd rather you go, "we need to have such and such class or add this and that", 
rather than you go ahead and dump all of this in one go. Show me your thought process, shorter outputs if you must.
```

### Questioning how the AI arrived at suggested values:

![img.png](screenshots/img1.png)

### Rejecting a suggested design because of a disliked pattern:

The AI proposed that `CombinedRating` return `null` for the score when a film has no votes. I said:

```
Can we do anything else other than "null"? I dislike returning null
```

We used `Optional<Double>` instead, so the absence of a score is explicit in the type rather than a value that has
to be remembered to check.