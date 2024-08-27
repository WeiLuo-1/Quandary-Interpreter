mutable int main(int arg) {
  mutable Ref head = 1 . (2 . nil);
  mutable int i = 3;
  while (i <= arg) { /* Note that i starts at 3 :) */
    Ref second = (Ref)right(head);
    setRight(second, i . nil);
    setRight(head, head);
    head = second;
    free head;
    i = i + 1;
  }
  return isNil(head);
}