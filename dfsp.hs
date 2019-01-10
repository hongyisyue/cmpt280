-- File Name: dfsp.hs

dfsp edges goal [] = []
dfsp edges goal ((c,p):choices)
    | goal == c  = Just (c)
    | otherwise  = dfsp edges goal (new ++ choices)
          where new = [ (v,c:p) | (u,v) <- edges, u == c]

