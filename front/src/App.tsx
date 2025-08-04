// src/App.tsx
import React from "react";
import { RouterProvider } from "react-router-dom";
import { router } from "./router/Router"; // 올바른 경로 확인

function App() {
  return <RouterProvider router={router} />;
}

export default App;
