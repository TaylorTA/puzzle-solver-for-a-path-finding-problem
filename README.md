# puzzle-solver-for-a-path-finding-problem
Standard input:
10,20
####################
#              @$$A#
#                  #
#                  #
#     .........    #
#     .:::::::.    #
#     .:!!!!!:.    #
# ####.:!$$$!:.    #
#   C#.:!$F$!:.    #
####################

Standard output:
Final state:
####################
#         1    @$$1#
#       11 1 1 122 #
#     11    1 1    #
#    1.........    #
#   1 .:::::::.    #
# 2211.:!!!!!:.    #
#2####1:11$$!:.    #
# 221#.1!$1$!:.    #
####################
State examined: 1435
Bomb disarmed: 3
Bomb exploded: 0
Cost of the plan: 45

The output would include all states disarmed bomb(letters) and final state. I successfully found a path to disarmed all bombs, but it was not the most efficient one. The program may not work for very big board. The program would quit if the size of states Queue was too big.
