# Pre-work - *Fancy ToDo App*

Fancy ToDo is an android app that allows building a todo list and basic todo items management functionality including adding new items, editing and deleting an existing item, and marking items as done.

Submitted by: Chris Spack

Time spent: 15 hours spent in total

## User Stories

The following **required** functionality is completed:

* [X] User can **successfully add and remove items** from the todo list
* [X] User can **tap a todo item in the list and bring up an edit screen for the todo item** and then have any changes to the text reflected in the todo list.
* [X] User can **persist todo items** and retrieve them properly on app restart

The following **optional** features are implemented:

* [X] Persist the todo items [into SQLite](http://guides.codepath
.com/android/Persisting-Data-to-the-Device#sqlite) instead of a text file
* [X] Improve style of the todo items in the list [using a custom adapter](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView)
* [ ] Add support for completion due dates for todo items (and display within listview item)
* [X] Use a [DialogFragment](http://guides.codepath.com/android/Using-DialogFragment) instead of new Activity for editing items
* [X] Add support for selecting the priority of each todo item (and display in listview item)
* [X] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [X] Added checkboxes to mark Todo items.
* [X] Added material design elements (RecyclerView, CardView, FloatingActionButton and TextInputLayout).
* [X] Added the ability to swipe away item cards, which also provides pretty animation of the list
view!

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/DGm21id.gif' title='Video Walkthrough' width='' alt='Video
Walkthrough' />


## Notes

Describe any challenges encountered while building the app.

* Adding a checkbox to the note list caused all the item click handlers to stop registering events. A quick search found the solution was to set 'focusable' to false to the checkbox.
* Androids Base64 class adds unnecessary line breaks which broke the file database (which splits by line). SQLite comes in the next commit.
* Making a demo video is tricky on linux, but licecap worked with Wine. :)
* I had trouble registering event handlers with RecycleView, so I had to move event handlers into the RecycleView.Adapter, which is a very bad design pattern. After further digging I found that there is addOnItemTouchListener which I plan to attempt later.
* Notify data set changed within event handlers causes exceptions with the UI thread. I moved to a delayed data set changes to a Runnable pattern.
* Most of my problems with data observation set were solved by refactoring to the TodoNoteDatabase
wrapper, which used an ObservableList pattern.

## License

    Copyright 2016 Chris Spack

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
