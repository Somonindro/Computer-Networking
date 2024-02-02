# simulator
set ns [new Simulator]


# ======================================================================
# Define options

set val(chan)         Channel/WirelessChannel   ;# channel type
set val(prop)         Propagation/TwoRayGround  ;# radio-propagation model
set val(ant)          Antenna/OmniAntenna       ;# Antenna type
set val(ll)           LL                        ;# Link layer type
set val(ifq)          CMUPriQueue               ;# Interface queue type
set val(ifqlen)       50                        ;# max packet in ifq
set val(netif)        Phy/WirelessPhy/802_15_4  ;# network interface type
set val(mac)          Mac/802_15_4              ;# MAC type
set val(rp)           DSR                       ;# ad-hoc routing protocol 
set val(nn) [lindex $argv 0]                    ;# number of mobilenodes
set val(as) [lindex $argv 1]                    ;# area size
set val(nf) [lindex $argv 2]                    ;# number of flows
# =======================================================================

# trace file
set trace_file [open trace.tr w]
$ns trace-all $trace_file

# nam file
set nam_file [open animation.nam w]
$ns namtrace-all-wireless $nam_file $val(as) $val(as) ;# area size should be given here


# topology: to keep track of node movements
set topo [new Topography]
$topo load_flatgrid $val(as) $val(as) ;# area size should be given here


# general operation director for mobilenodes
create-god $val(nn)


# node configs
# ======================================================================

# $ns node-config -addressingType flat or hierarchical or expanded
#                  -adhocRouting   DSDV or DSR or TORA
#                  -llType	   LL
#                  -macType	   Mac/802_11
#                  -propType	   "Propagation/TwoRayGround"
#                  -ifqType	   "Queue/DropTail/PriQueue"
#                  -ifqLen	   50
#                  -phyType	   "Phy/WirelessPhy"
#                  -antType	   "Antenna/OmniAntenna"
#                  -channelType    "Channel/WirelessChannel"
#                  -topoInstance   $topo
#                  -energyModel    "EnergyModel"
#                  -initialEnergy  (in Joules)
#                  -rxPower        (in W)
#                  -txPower        (in W)
#                  -agentTrace     ON or OFF
#                  -routerTrace    ON or OFF
#                  -macTrace       ON or OFF
#                  -movementTrace  ON or OFF

# ======================================================================

# setting same configuration to all nodes
$ns node-config -adhocRouting $val(rp) \
                -llType $val(ll) \
                -macType $val(mac) \
                -ifqType $val(ifq) \
                -ifqLen $val(ifqlen) \
                -antType $val(ant) \
                -propType $val(prop) \
                -phyType $val(netif) \
                -topoInstance $topo \
                -channelType $val(chan) \
                -agentTrace ON \
                -routerTrace ON \
                -macTrace OFF \
                -movementTrace OFF


if {$val(nn) == 20} {
    set col 5
} elseif {$val(nn) == 40} {
    set col 8
} else {
    set col 10
}


# create nodes
set count 0
for {set i 0} {$i < [expr $val(nn)/$col] } {incr i} {
    for {set j 0} { $j < $col } { incr j } {
       set node($count) [$ns node]
       $node($count) random-motion 0       ;# disable random motion

       set y [expr $val(as)/$val(nn) * $i *5 +10]
       set x [expr $val(as)/$val(nn) * $j *5 +10]

       #puts "x= $x and y= $y"

       $node($count) set Y_ $y
       $node($count) set X_ $x
       $node($count) set Z_ 0

       $ns initial_node_pos $node($count) 20 
       incr count

    }
    
} 



# producing node movements with uniform random speed
for {set i 0} {$i < $val(nn)} {incr i} {
    $ns at 10.0 "$node($i) setdest [expr int(10000 * rand()) % $val(as) + 0.5] [expr int(10000 * rand()) % $val(as) + 0.5] [expr int(100 * rand()) % 5 + 1]"
}



# Traffic
# picking random source node
set src [expr int(1000 * rand()) % $val(nn)]

for {set i 0} {$i < $val(nf)} {incr i} {
    
    # picking random destination/sink node
    while {1} {
        set dest [expr int(1000 * rand()) % $val(nn)]
        if {$src != $dest} {
            break
        }
    }

    # Traffic config
    # create agent
    set tcp [new Agent/TCP/Reno]
    set tcp_sink [new Agent/TCPSink]
    # attach to nodes
    $ns attach-agent $node($src) $tcp
    $ns attach-agent $node($dest) $tcp_sink
    # connect agents
    $ns connect $tcp $tcp_sink
    $tcp set fid_ $i
    $tcp set packetSize_ 512
    #$tcp set rate_ 1mb

    # Traffic generator
    set ftp [new Application/FTP]
    # attach to agent
    $ftp attach-agent $tcp
    
    # start traffic generation
    $ns at 1.0 "$ftp start"
}



# End Simulation

# Stop nodes
for {set i 0} {$i < $val(nn)} {incr i} {
    $ns at 50.0 "$node($i) reset"
}

# call final function
proc finish {} {
    global ns trace_file nam_file
    $ns flush-trace
    close $trace_file
    close $nam_file
}

proc halt_simulation {} {
    global ns
    puts "Simulation ending"
    $ns halt
}

$ns at 50.0001 "finish"
$ns at 50.0002 "halt_simulation"



# Run simulation
puts "Simulation starting"
$ns run

