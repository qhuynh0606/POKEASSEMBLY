# PokeAssembly Example 3: Status, weather, team heal, and catch

.data
status_msg:
.asciiz "You applied and cleared some status effects.\n"

weather_msg:
.asciiz "The weather changed!\n"

heal_msg:
.asciiz "Your whole team was healed!\n"

switch_msg:
.asciiz "You switched your lead Pokemon.\n"

catch_good:
.asciiz "You have a good chance to catch it!\n"

catch_bad:
.asciiz "The catch chance is low...\n"

.text
.globl main

# Assume:
# $t0 = party mon 1 HP
# $t1 = party mon 2 HP
# $t2 = party mon 3 HP
# $t3 = party mon 4 HP
# $t4 = wild Pokemon HP
# $t5 = catch chance
# $t6 = status flags
# $t7 = temp / fake jump flag

main:

#  Party + wild HP
hpup $t0, 10      # mon 1 HP = 10
hpup $t1, 5       # mon 2 HP = 5
hpup $t2, 1       # mon 3 HP = 1
hpup $t3, 0       # mon 4 HP = 0
hpup $t4, 40      # wild mon HP = 40


#  Status demo

# Bitmask idea:
# 0x01 = SLEEP, 0x02 = POISON, etc.
# status in $t6

# Start with all status bits cleared
statclr $t6, -1     # clear all bits (mask = -1 effectively)

# Apply SLEEP and POISON
statset $t6, 0x01       # add SLEEP
statset $t6, 0x02       # add POISON

# Clear SLEEP, keep POISON
statclr $t6, 0x01

trainer status_msg

# weather code in $t7, e.g. 1 = rain, 2 = sun, etc.
weather $t7, 2          # set to "2" (e.g. sunny)
trainer weather_msg

#  Team-wide healing
# Heal everyone by 5 HP using gheal
hpup  $t5, 5            # $t5 = heal amount
gheal $t5               # add 5 to registers 0..15 (includes t0–t3)
trainer heal_msg


#  Switch lead Pokemon
# Swap HP between $t0 (lead) and $t1 (bench)
switch $t0, $t1
trainer switch_msg

#  Catch chance demo

# Compute catch chance based on current wild HP
catch $t5, $t4         # $t5 = 255 - HP (clamped 0..255)

# Now "weaken" the wild mon to make chance good
hpdown $t4, 30          # wild HP: 40 -> 10

# Manually flag that we consider this a "good" chance
statclr $t7, -1     # clear flag
statset $t7, 0x01       # set bit => non-zero

# If flag non-zero, branch to good_chance
bstatus $t7, good_chance

# Otherwise (if flag were zero), low chance path
trainer catch_bad

# Fake j end using bstatus
hpup $t7, 1
bstatus $t7, end

good_chance:
trainer catch_good

end:

