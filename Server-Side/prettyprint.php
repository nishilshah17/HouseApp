<?php
function json_pretty($json, $html = false) {
    $out = ''; $nl = "\n"; $cnt = 0; $tab = 4; $len = strlen($json); $space = ' ';
    if($html) {
        $space = '&nbsp;';
        $nl = '<br/>';
    }
    $k = strlen($space)?strlen($space):1;
    for ($i=0; $i<=$len; $i++) {
        $char = substr($json, $i, 1);
        if($char == '}' || $char == ']') {
            $cnt --;
            $out .= $nl . str_pad('', ($tab * $cnt * $k), $space);
        } else if($char == '{' || $char == '[') {
            $cnt ++;
        }
        $out .= $char;
        if($char == ',' || $char == '{' || $char == '[') {
            $out .= $nl . str_pad('', ($tab * $cnt * $k), $space);
        }
        if($char == ':') {
            $out .= ' ';
        }
    }
    return $out;
}

?>