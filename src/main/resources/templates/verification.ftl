<#import "defaultLayout.ftl" as layout>
<@layout.myLayout>
    <div>
        <table align="center" border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse;">
            <tr>
                <td style="padding: 0px 30px 0px 30px;">
                    <p>Dear ${name},</p>
                    <p>${msg}</p>
                </td>
            </tr>
            <tr>
                <td style="padding-left: 30px;">
                    <p>
                        Cheers, <br /> <em>RouteMaster Admin</em>
                    </p>
                </td>
            </tr>
        </table>
    </div>
</@layout.myLayout>