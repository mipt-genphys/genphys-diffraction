package ru.mipt.phys.diffraction


/**
 * Алгоритм заполнения контура
 */
fun Array<IntArray>.fillcontour() {
    val table = this
    for (i in 1 until table.size - 1) {
        var state = 0
        var firstZero = -1
        var firstOne = -1
        var j = 1
        var firstOneLocked = 0
        while (j < table.size - 1) {
            if (table[i][j] == 1) {
                if (table[i][j + 1] == 1) {
                    if (firstOneLocked == 0) {
                        firstOne = j
                        firstOneLocked = 1
                    }
                    if (table[i][j + 2] == 0) {
                        if ((((table[i + 1][firstOne] == 1) or (table[i + 1][firstOne - 1] == 1)) and
                                    ((table[i - 1][j + 1] == 1) or (table[i - 1][j + 2] == 1))) or (((table[i - 1][firstOne] == 1) or
                                    (table[i - 1][firstOne - 1] == 1)) and
                                    ((table[i + 1][j + 1] == 1) or (table[i + 1][j + 2] == 1)))
                        ) {
                            if (state == 0) {
                                state = 1
                                firstZero = j + 2
                                j += 2
                                firstOne = -1
                                firstOneLocked = 0
                                continue
                            } else {
                                for (k in firstZero until firstOne) {
                                    table[i][k] = 2
                                }
                                firstZero = -1
                                state = 0
                                j += 2
                                firstOne = -1
                                firstOneLocked = 0
                                continue
                            }
                        } else if (state == 0) {
                            j += 2
                            firstOne = -1
                            firstOneLocked = 0
                            continue
                        } else {
                            for (k in firstZero until firstOne) {
                                table[i][k] = 2
                            }
                            firstZero = j + 2
                            j += 2
                            firstOne = -1
                            firstOneLocked = 0
                            continue
                        }
                    }
                    j += 1
                    continue
                }
                if ((table[i][j + 1] == 0) and (state == 0)) {
                    firstZero = j + 1
                    state = 1
                } else if ((firstZero >= 0) and (state == 1)) {
                    for (k in firstZero until j) {
                        table[i][k] = 2
                    }
                    firstZero = -1
                    state = 0
                }
            }
            j += 1
        }
    }
}