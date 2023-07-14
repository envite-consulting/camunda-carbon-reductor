# Contribution Guidelines

Ahoy there, aspiring contributor! We wholeheartedly welcome your desire to make a marvelous contribution to our 
glorious endeavor. However, we beseech thee to peruse this illustrious document before embarking on your quest, for 
it holds the key to understanding the most formidable ways to present thy pull requests. It encompasses the sacred 
knowledge of the changes that are likely to be embraced by our esteemed core developers and enlightens thee on what to 
expect when thy submission undergoes the scrutiny of their discerning eyes.

Thus, we implore thee to employ this document as a mighty checklist ere thou unleash thy pull request upon the world. 
It shall bestow upon thee the precious gift of time saved for all parties involved in this grand venture. 
We extend our deepest gratitude for thy compliance, noble soul!

<!-- TOC -->
* [Create your pull request](#create-your-pull-request)
  * [What is a pull request?](#what-is-a-pull-request)
  * [Greetings, visionary creator of grand ideas!](#greetings-visionary-creator-of-grand-ideas)
  * [Git commits: Kindly grace us with your true appellation](#git-commits-kindly-grace-us-with-your-true-appellation)
  * [Format commit messages](#format-commit-messages)
  * [Clean up commit history & Rebase](#clean-up-commit-history--rebase)
  * [Prepare thyself for the captivating dance of discussion and refinement!](#prepare-thyself-for-the-captivating-dance-of-discussion-and-refinement)
* [Thrilled to contribute: Get started](#thrilled-to-contribute-get-started)
<!-- TOC -->

## Create your pull request

### What is a pull request?

Ahoy, wanderer of the digital realms! Should thou find thyself perplexed by the enigmatic art of pull requests or 
the sacred rituals of their submission, I shall point thee toward a trove of wisdom crafted by the sages of GitHub. 
Venture forth to https://help.github.com/articles/about-pull-requests, where an illustrious compendium awaits thee, 
brimming with knowledge and sagacity. This veritable treasure trove shall bestow upon thee a splendid overview of
pull requests and their arcane submission process, illuminating thy path to enlightenment.

### Greetings, visionary creator of grand ideas!

If thou dost possess a concept of great magnitude that transcends the mere realm of fixing typos or vanquishing minor 
bugs, tis prudent to engage in conversation with the esteemed core developers ere thou dispatch thy majestic pull 
request. Fear not, for we are delighted to impart our wisdom and provide thee with sage guidance. However, we do 
beseech thee to dedicate an hour or two to researching thy chosen topic and delving into the annals of past discussions. 
This noble quest shall equip thee with knowledge and understanding ere thou doth summon our aid, allowing for a more 
fruitful exchange betwixt us.


### Git commits: Kindly grace us with your true appellation

To ensure your commits bear the mark of your true identity when submitting those delightful pull requests, bestow 
upon Git the honor of knowing your genuine first and last name. 

This universal configuration can be accomplished thusly:
```
git config --global user.name "John Doe"
git config --global user.email john.doe@example.com
```

For information how to set things up, see https://help.github.com/articles/set-up-git/.

### Format commit messages

Most importantly, please format your commit messages in the following way:

* Prefix Git commit messages with the ticket number, e.g. "Close #42: xyz"
* Describe WHY you are making the change, e.g. "Close #42: Added logback to suppress the debug messages during maven build" (not only "changed logging").

### Clean up commit history & Rebase

Unleash the power of `git rebase --interactive` to forge thy mighty commits into exquisite atomic changes, merging 
them with grace and sensibility. Behold, beyond the sacred man pages of git lie abundant online resources that 
shall illuminate the intricate workings of these mystical tools.

In thy noble quest, remember to eternally tether thy (enhanced) pull request to the ever-evolving `main`, intertwining 
thy branches through the ancient rite of rebasing. Thus, I beseech thee, refrain from fashioning pull requests that 
lean upon the frail shoulders of their brethren. Instead, unite all thy noble commits within a singular pull request, 
or bide thy time until the dependent pull request hath merged into the cosmic fabric of our codebase.

### Prepare thyself for the captivating dance of discussion and refinement!

The noble core team shall bestow their discerning gaze upon thy contributions, for they seek to uphold the grandeur 
of code quality and the bastions of stability, while taming the wild beast of complexity. Be not surprised if thou art 
summoned to reshape thy submission, both in style (as previously elucidated) and substance.

Behold, the continuous integration realm shall weave its magic, building and testing thy resplendent pull request, 
putting its mettle to the ultimate trial.

Take heed, for in thy journey, thou art granted the power to force push (`git push -f`) thy reworked and rebased commits
unto the very branch that bore thy original pull request. Hence, fear not the arduous task of issuing a new pull 
request when the clarion call for changes resounds in thy ears.

## Thrilled to contribute: Get started

Rejoice, for your heart yearns to contribute, yet the path eludes you! Fret not, for I shall unveil a guiding light 
amidst the vast cosmos of opportunities. Direct your gaze towards the open issues, located at the sacred domain 
of https://github.com/envite-consulting/camunda-carbon-reductor/issues. Therein lies a treasure trove of challenges, 
beckoning your eager spirit to embark upon a grand quest.

We eagerly anticipate the grandeur of your forthcoming contribution, accompanied by an exuberant dose of anticipation 
and a dash of wild excitement! 🚀🌱