# ALISON V2

[![Project Status: Active](https://www.repostatus.org/badges/latest/active.svg)](https://www.repostatus.org/#active) [![GPLv3 license](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![Build Status](https://jenkins.voidtech.de/buildStatus/icon?job=Alison)](https://jenkins.voidtech.de/job/Alison/)

Automatic Learning Intelligent Sentence Organising Network (Version 2)

ALISON is a data-centric sentence generator that uses Discord guild messages to continuously learn how to create imitation messages, as well as using sentiment analysis to provide judgements of said messages.

ALISON uses:
- Java because it's easy
- A custom model format using serialized Java collections (this was an awful mistake, but it kinda worked)
- JDA to talk to Discord
- WRTT Sentence Generation
- OAT Sentiment determination
- Hopes and dreams

## Why does ALISON V2 exist?

V1 was getting slow due to some poor design decisions. Instead of refactoring, I took this opportunity to start afresh, using a very different architecture as a learning experience, as well as incorporating new and better functionality.

## What ALISON *isn't*

~~ALISON is not a chatbot~~

That was a lie. I added this functionality (although it's remarkably scuffed). I would still recommend you visit my good friend [Scot_Survivor](https://github.com/Scot-Survivor) and check out his work on [Gavin Development](https://github.com/Gavin-Development).

## What ALISON *is*

ALISON is a series of different NLP technologies crammed together into one bot. None of them are particularly complex, anyone could understand the theory in just a few moments. Having said that, here's the theory:

### A Markov Chain Sentence Generator

The pride and joy of ALISON's ensemble is her ability to learn from your Discord messages and speak like you. She does this by following a very simple algorithm.

Learning is easy:

1) Receive a text message
2) Split the message into tokens (individual words)
3) Add the tokens to a database

Generation is a bit more involved:

1) Pick a random start token
2) Using this token, get a list of *all* the tokens in the database for this user which start with this token
3) Create a list which contains all these tokens, where each token occurs `n` times, `n` being the number of times you have used this combination before.
4) Pick a random next token from this list
5) Repeat until we reach a stop word
6) Send the sentence back to the user

ALISON's markov chain implementation is by no means the first of its kind, however I refer to the algorithm as WRTT (**W**eighted **R**andom **T**ree **T**raversal)

### A Sentiment Analyser

ALISON's Sentiment Analyser is based around a dataset of approximately 5000 negative words and 2000 positive words. To score a piece of text, we can simply search the text for any occurrences of a known word or phrase and do some mathematical goodness.

As before, here is a breakdown of the sentiment algorithm:

1) Remove all non-alphabetic chars from the text
2) Iterate through the list of known words. Each time a word is found in the text, add this word to either a `positives` list or a `negatives` list `n` times, where `n` is the number of occurrences of this word.

Once all this goodness is done, we calculate several scores. `Total`, `Average` and `Adjusted`.

`Total`: gives an overall score for a piece of text based on the positives and negatives

Total is easy to calculate, just subtract the count of negative words from the count of positive words.

`Average`: calculates the average score for a piece of text

Average is also easy, just divide the previous score by the number of positives and negatives.

`Adjusted`: gives an overall score but will benefit or detriment a score based on the ratio of positives to negatives (used to make a sentiment decision)

Adjusted is a bit more involved...

If there are no positives or negatives, the score is 0.
If there are no positives but some negatives, add the number of negatives multiplied by -1 to the total score
If there are no negatives but some positives, add the number of positives multiplied by +1 to the total score
If there are more positives than negatives, calculate the number of positives minus the number of negatives, then multiply by +1 and add to the total score
If there are more negatives than positives, calculate the number of negatives minus the number of positives, then multiply by -1 and add to the total score

OAT is by no means accurate. But it does the job in an entertaining manner, which is the point of this entire program. OAT stands for **O**bjective **A**nalysis of **T**ext

### A Levenshtein Calculator

This piece of NLP goodness serves only one purpose. Help people if they misspell a command. It works a bit like this:

1) A user enters a command which has been spelt wrong
2) Alison compares this command name to every known command name
3) She finds the levenshtein distance between this command and the known commands
4) The first one which has a levenshtein distance <= 2 will be chosen as a suggestion

### A "Chatbot"

Alison now contains a chatbot. Sort of. It uses another funky homemade algorithm called CLAIRE (Continuous Learning Artificial Intelligence Response Engine) which works very similarly to the WRTT algorithm, only with a few minor changes.

CLAIRE is split into two seperate entities, bonded together by a resource pool (in this case, an SQLite database). 

#### Continuous Learning

Since Alison is a Discord bot, she has access to Discord messages. As such, messages with relevant replies can be easily gathered to use as a dataset. This process is very simple.

1) A user sends a message
2) Another user replies to this message
3) Alison notices the reply and copies the content of both the original message and the reply into a resource pool (anonymously)

#### Artificial Intelligence Response Engine

When someone provides Alison with a prompt, she uses the provided message to find contextual responses to the original prompt, which she then tokenises and blends together to make a response. This is very similar to WRTT, but for the sake of coherent responses, this particular algorithm uses WPTT (Weighted Predictable Tree Traversal) instead.

1) A user gives Alison a prompt
2) The prompt is divided into inividual words
3) The resource pool is searched for these words, and a message pool is created
4) The messages in the message pool are full sentences, so they need to be tokenised into a list of `AlisonWord` first
5) Once a tokenised list is generated, it can be fed into a WPTT sentence generator. WPTT is identical to WRTT (explained above) but instead of randomly choosing the next word from a pool, the next word is determined by the frequency of the `AlisonWord` in the tokenised pool.
### Conclusion

Thus concludes this epic tale, A Research Paper Called ALISON. I am not a data scientist, nor am I in any way an AI/ML or NLP expert. I am fully aware that ALISON is not very smart, but I personally think this makes her more entertaining. ALISON is not meant to be accurate, she's meant to be amusing.

If you want to see her working, you will need to run your own instance as mine is not publicly available for privacy reasons.

## Running ALISON

Running ALISON is not a difficult task, however you will need some prerequisites. I recommend:

- Eclipse IDE (to compile ALISON)
- Java SE 8

### Building

1) Pull or clone this repository
2) Import the pom.xml into Eclipse
3) Create a build run config with the goals `clean package --debug` for ALISON
4) Build ALISON using this run config

### Running

ALISON is a smarty, so she will automatically create her databases. You will however need to provide her with a configuration file.

Your config file needs to be called `AlisonConfig.properties` and it needs to be placed at either the root of the project, or directly next to a compiled jar. It should contain these fields:

```
Prefix=[a prefix]
Token=[Discord bot token]
```

With this file created and in the right place, Alison should now come crashing into existence for your enjoyment.

Alison does not come with any tech support. She has no support server, no forum and my instance is not available to the public.

## Creating custom models

If you want to use an already-existing corpus to create a model, you will need [ALISON-Trainer](https://github.com/Elementalmp4/ALISON-Trainer)

Follow the steps in the trainer repository to create a custom model. 