# City plugin for minecraft spigot 1.16
This is a simple city protection plugin for spigot 1.16. It allows the creation of 
city regions, in which only residents of the city can build.

## Commands

Here is a short list of the commands

* ```/city``` is the base command which will display all subcommands available to you
* ``/city create <cityname>`` creates a new city and adds you as a resident.
* ``/city remove <cityname>`` deletes the city with the given name
* ``/city list`` lists all cities
* ``/city map`` Shows a map of chunks at the left scoreboard, to visualize claimed chunks that belong to cities.
* ``/city info`` Shows info for the city you are a resident in.
* ``/city chunks <add|remove>`` Adds or removes your current chunk to your city.
* ``/city residents <<add|remove> <playename>|list [page]>`` Adds or removes a player from your city as a resident, or lists all your cities residents.
* ``/city admin <cityname> <subcommand>`` This allows admins to edit cities (chunks, residents) without needing to be a resident of it.

## Permissions
This section lists the permissions of the plugin.

* ``cities.city`` Allow the use of the standard city command. Default everyone
    * ``cities.city.list`` Allows the use of the city list command. Default everyone
    * ``cities.city.info`` Allows the use of the city info command. Default everyone
* ``cities.city.admin`` The admin permission which allows you to build in any city, and use the city admin command. Default admins
    * ``cities.city.map`` Allows the usage of /city map. Default admins
    * ``cities.city.chunks`` Allows the usage of /city chunks. Default admins
    * ``cities.city.residents`` Allows the usage of /city residents. Default admins
    * ``cities.city.delete`` Allows the usage of /city delete. Default admins
    * ``cities.city.create`` Allows users to create a city. Default admins