BitCamp demo app for play
=================================

<h1> Version 1 </h1>

Currently the app contains what we have covered in the past week doing Play!.
You can find a user model and post model with some naive security implemented.
Take a not of partial views such as navigation.scala.html, _userForm.scala.html and _postForm.scala.html.
You will see some inconsistency in handling some functionality, this is bad in the general case but here it server
to expose different ways to achieve the same thing.


<h2> Version 2 </h2>

Added contact form and connected the mailer-plugin. You should take a look at the conf/application.conf file.
<b> Add a conf/reference.conf file and set the smtp.username and smtrp.password variables there. </b>
<i> Don't forget to add the cong/reference.conf file to your .gitignore.

<h3> Version 3 </h3>

We have added i18n messages to the project and an admin filter.
