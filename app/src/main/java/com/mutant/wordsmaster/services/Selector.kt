package com.mutant.wordsmaster.services

class Selector {

    object Title {

        const val outer = "div.gt-cd-tl"

        const val title = "span"
    }

    object Definition {

        const val outer = "div.gt-cd-c"

        // Part of speech
        const val cdPos = "div.gt-cd-pos"

        // Definition
        const val defRow = "div.gt-def-row"

        // Definition example
        const val defExample = "div.gt-def-example"
    }

    object Example {

        const val outer = "div.gt-ex-text"

    }
}