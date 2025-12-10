# PokeAssembly Example 1: Simple attack + faint check

.data
# Messages
hit_msg:
.asciiz "You hit the OPP\n"

still_alive:
.asciiz "OPP is still alive!\n"

faint_msg:
.asciiz "OPP freaking died lmaooo\n"

.text
.globl main

# Assume:
# $t0 = your HP
# $t1 = your ATK
# $t2 = your DEF
# $t3 = OPP HP
# $t4 = OPP ATK
# $t5 = OPP DEF
# $t6 = dmg

main:
# initial stats
hpup $t0, 50	# your HP = 50
atkup $t1, 20	# your ATK = 20
defup $t2, 10	# your DEF = 10
hpup $t3, 40	# opp HP = 40
atkup $t4, 20	# opp ATK = 20
defup $t5, 12	# opp DEF = 12

# calculate dmg from our attack
atk $t6, $t1, $t5	# $t6 = dmg

# apply said damage
appdmg $t3, $t6		# OPP HP -= dmg

# print hit message
trainer hit_msg

# check if OPP fainted
faint $t3, opp_fainted

# but if enemy still alive
trainer still_alive

# temp j end
hpup $t7, 1	# $t7 = 1
bstatus $t7, end

opp_fainted:
trainer faint_msg

end: