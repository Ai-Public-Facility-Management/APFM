// src/components/Layout.tsx
import React from "react";
import TopBanner from "./TopBanner";
import Header from "./Header";
import Footer from "./Footer";

interface Props {
  children: React.ReactNode;
}

const Layout = ({ children }: Props) => {
  return (
    <div className="container">
      <TopBanner />
      <Header />
      <main className="mainContent">{children}</main>
      <Footer />
    </div>
  );
};

export default Layout;
