publi class Main{
  public static void main(String[] args) {
            String[] arr = {"Мирсаит", "Камиль", "Аскар", "Малик", "Саид"};

            Arrays.sort(arr, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return lexCompare(s1, s2);
                }
            });

            System.out.println(Arrays.toString(arr));
        }

        static int lexCompare(String a, String b) {
            int n = Math.min(a.length(), b.length());

            for (int i = 0; i < n; i++) {
                char c1 = a.charAt(i);
                char c2 = b.charAt(i);

                if (c1 != c2) {
                    return c1 - c2;
                }
            }

            return a.length() - b.length();
        }
}
