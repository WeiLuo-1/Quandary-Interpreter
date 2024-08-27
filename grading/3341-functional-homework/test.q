/* Q1 */
int isList(Q x) {
    if (isAtom(x) != 0) {
        if (isNil(x) != 0) {
            return 1;
        }
        return 0;
    }
    return isList(right((Ref)x));
}

/* Q2 */
Ref append(Ref list1, Ref list2) {
    if (isNil(list1) != 0) {
        return list2;
    }
    return left(list1) . append((Ref)right(list1), list2);
}

/* Q3 */
Ref reverse(Ref list) {
    if (isNil(list) != 0) {
        return list;
    }
    Ref tempList = reverse((Ref)right(list));
    return append(tempList, left(list) . nil);
}

/* Q4 */
int count(Ref list) {
    if (isNil(list) != 0) {
        return 0;
    }
    int c = 1;
    return c + count((Ref)right(list));
}
int isSorted(Ref list) {
    if (isNil(list) != 0 || isNil(right(list)) != 0) {
        return 1;
    }
    if (count((Ref)left(list)) <= count((Ref)left((Ref)right(list)))) {
        return isSorted((Ref)right(list));
    }
    return 0;
}

/* Q7 */
int sameLength(Ref list1, Ref list2) {
    if (isNil(list1) == 0 && isNil(list2) == 0) {
        return sameLength((Ref)right(list1), (Ref)right(list2));
    }
    if (isNil(list1) != 0 && isNil(list2) != 0) {
        return 1;
    }
    return 0;
}


int genericEquals(Q item1, Q item2) {
    if (isNil(item1) != isNil(item2)) {
        return 0;
    } else {
        if (isNil(item1) == 1) {
            return 1;
        }
    }
    if (isAtom(item1) != isAtom(item2)) {
        return 0;
    } else {
        if (isAtom(item1) == 1) {
            if ((int)item1 == (int)item2) { /* ??? */
                return 1;
            } else {
                return 0;
            }
        }
    }
    /* item1 and item2 are Ref's */
    if (genericEquals(left((Ref)item1), left((Ref)item2)) == 1 && genericEquals(right((Ref)item1), right((Ref)item2)) == 1) {
        return 1;
    }
    return 0;
}



int main(int arg) {
    Ref input = (nil . ((314 . nil) . ((15 . nil) . ((926 . (535 . (89 . (79 . nil)))) . ((3 . (2 . (3 . (8 . (4 . nil))))) . nil))))); /* Complicated example */
    if (isSorted(input) != 0) {
        return 1;
    }
    return 0;
}

