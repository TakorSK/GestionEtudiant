TO organize our work Please log what you do in this file.
MAKE SURE THAT WHENEVER YOU PUSH YOUR WORK IT DOES NOT CONTAIN ANY ERRORS THAT IMPACT PERFORMANCE AND THAT EVERYTHING SHOULD BE FUNCTIONAL. IF YOU ENCOUNTER SOME KIND OF AN ERROR PLEASE UPDATE YOUR STUDIO. IF IT PERSISTS PLEASE CONTACT ME.

======================================== ADAM ========================================
TODO:
    - Add a "target" list in CreatePost fragment (e.g. all, unis, admins)         [Medium]
    - Add Schedule display and interaction logic                                  [High]
    - Add Scores display section                                                  [High]
    - Add Settings: profile info edit (e.g. name, email)                          [Optional]
    - Add Upload/Sign-in loading animations and disable button during process     [Medium]
    - Add a "tag" field to posts to know who can look and who can't (for admin, it's tag uniAdmins, students and clubs. for uni it's this.uni means members of the uni, so students. or all, wich would just be a public post for all unis and students and clubs. for club it would be same thing, this.club or all (when we start doing clubs seriously))               [Low]"
    - Fix drawer display for each account type                                    [High]

DONE:
     - Add "Add a Student" layout, DB, and logic
     - Add Admin Board fragment
     - Add Clubs display logic
     - Add Clubs Search Tag and Student adding functionality in the admin panel
     - Add Corresponding DB fields to admin panel buttons
     - Add Dark Mode / Light Mode + settings persistence
     - Add Drawer navigation
     - Add Drop Shadows
     - Add error messages to input fields (e.g. red text under invalid fields)
     - Add No Internet Popup dialog
     - Add Post Author and Date
     - Add Post Sorting by Date
     - Add Posts layout and DB logic
     - Add Profile Activity (with all functionalities)
     - Add Search hint text to include instructions for 'club:' prefix
     - Add Search Functionality UI
     - Add Settings: Bio support
     - Add Settings: Profile picture upload
     - Add Settings: Theme toggle (light/dark)
     - Add UI Fragments (all)
     - Add University Management fragment (posts)

     - Refactor search filtering logic to support clubs in addition to students and universities

     - Fix Admin Panel shadow clipping
     - Fix Admin Panel UI drop shadows clipping/styling
     - Fix Add Student, Clubs & University layouts
     - Fix Club field optional, University required in Signup
     - Fix ColorPrimary mismatch
     - Fix Dark Mode on input fields
     - Fix Drawer navigation selection color
     - Fix Fields color mismatch & darkness
     - Fix Input fields to single line
     - Fix Keyboard 'Next' action on text inputs
     - Fix Light/Dark mode toggle + persistence
     - Fix Login functionality
     - Fix Posts layout (poster section more defined)
     - Fix Settings profile image mask
     - Fix Signup activity (updated XML)
     - Fix Signup fields logic + layout
     - Fix UI text colors

     - Update UI layouts to include club-related views and buttons
     - Update UI layouts to include ID field upon clicking the student item on the search bar


=======================================================================================



====================================== SOUFIANE =======================================
TODO
    - Add club (and maybe uni?)
    - Integrate the DB With the ADMIN menu (Affected stuff; AdminFragment, DummyStudent and DummyUniversities should be removed once you get the db working.)
    - Make the connexion last even after closing the app, it doesn't disconnect you
    - Fix the settings
    - Link the DB to the search on Admin Pannel (its using mock data rn in adminpannelfragment.java i think)
    - Fix posting post-firebase changes
    - make a tag system: universities can only post either for their default tag, or all. clubs can only post to their members, or all. and admins can post for admins only, universities only, students from a certain university tag or all.

DONE:
    - edited MainActivity.java and SignupActivity.java for the room database usage -Done
    - edited the database for compatibility with the xml form, we forgot to put the password in the database lmao -Done
    - edited the whole signup activity,uniflowDB and 'databaseclient' after that -done
    - Test The Database -done
    - Insert directly into database universities and clubs for testing -done
    - Added synchronization for the is_online and last_login columns in the database -done
    - synchronized profile(image and text) partially -done
    - Linking the database to the app -done
    - updated sign in to be online after a successful registration -done
    - implemented fully the synchronization with the database for profile -done
    - made it so only students with is_admin=true can access the admin panel -done
    - make and synchronize images to students -done
    - polish signups and logins inputs (better conditions to control the coherence of data put inside of the db) -done
    - Change database to firebase

=======================================================================================



======================================== ALI ==========================================
TODO
    - TESTING EACH FUNCTION.
    - Adding verification logic of each field input.
    - adding verification to all table

DONE:

=======================================================================================