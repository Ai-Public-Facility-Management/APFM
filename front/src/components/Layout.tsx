// src/components/Layout.tsx
import React from "react";
import TopBanner from "./TopBanner";
import Header from "./Header";
import Footer from "./Footer";

interface Props {
    children: React.ReactNode;
    mainClassName?: string; // 페이지별 main 스타일 적용
}

const Layout = ({ children, mainClassName }: Props) => {
    return (
        <div className="container">
            <TopBanner />
            <Header />
            <main className={mainClassName}>{children}</main>
            <Footer />
        </div>
    );
};

export default Layout;