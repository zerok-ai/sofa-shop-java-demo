import { Fragment } from "react"
import CartLinkWithContext from "./CartLink"
import Head from "next/head"
import appDetails from "../utils/appDetails"

const PageHeader = () => {
  return (
    <Fragment>
      <CartLinkWithContext />
      <Head>
        <title>{appDetails.title}</title>
        <meta name="description" content={appDetails.title} />
        <meta property="og:title" content={appDetails.title} key="title" />
      </Head>
    </Fragment>
  )
}

export default PageHeader
