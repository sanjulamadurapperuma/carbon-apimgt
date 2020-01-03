/**
 * Copyright (c)  WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useState, useEffect, useContext } from 'react';
import PropTypes from 'prop-types';
import { Grid, TextField, MenuItem } from '@material-ui/core';
import { FormattedMessage, injectIntl } from 'react-intl';
import { isRestricted } from 'AppData/AuthManager';
import APIContext from 'AppComponents/Apis/Details/components/ApiContext';

/**
 * The base component for advanced endpoint configurations.
 * @param {any} props The props that are being passed
 * @returns {any} The html representation of the component.
 */
function EndpointSecurity(props) {
    const { api } = useContext(APIContext);
    const { intl, securityInfo, onChangeEndpointAuth } = props;
    const [endpointSecurityInfo, setEndpointSecurityInfo] = useState({
        type: 'BASIC',
        username: '',
        password: '',
        tokenUrl: '',
        apiKey: '',
        apiSecret: '',
        grantType: '',
    });
    const [securityValidity, setSecurityValidity] = useState();
    const [selectedAuthType, setSelectedAuthType] = useState();

    const authTypes = [
        {
            key: 'BASIC',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.basic',
                defaultMessage: 'Basic Auth',
            }),
        },
        {
            key: 'DIGEST',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.digest.auth',
                defaultMessage: 'Digest Auth',
            }),
        },
        {
            key: 'OAUTH',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth',
                defaultMessage: 'OAuth',
            }),
        },
    ];
    useEffect(() => {
        const tmpSecurity = {};
        if (securityInfo !== null) {
            const {
                type, username, password, tokenUrl, apiKey, apiSecret, grantType,
            } = securityInfo;
            tmpSecurity.type = type;
            tmpSecurity.username = username;
            tmpSecurity.password = password === '' ? '**********' : password;// TODO - Check if the asterisks are needed
            tmpSecurity.tokenUrl = tokenUrl;
            tmpSecurity.apiKey = apiKey;
            tmpSecurity.apiSecret = apiSecret;
            tmpSecurity.grantType = grantType;
        }
        setEndpointSecurityInfo(tmpSecurity);
    }, [props]);

    const validateAndUpdateSecurityInfo = (field) => {
        if (!endpointSecurityInfo[field]) {
            setSecurityValidity({ ...securityValidity, [field]: false });
        } else {
            setSecurityValidity({ ...securityValidity, [field]: true });
        }
        onChangeEndpointAuth(endpointSecurityInfo[field], field);
    };
    return (
        <Grid container direction='row' spacing={2}>
            <Grid item xs={6}>
                <TextField
                    disabled={isRestricted(['apim:api_create'], api)}
                    fullWidth
                    select
                    value={endpointSecurityInfo.type}
                    variant='outlined'
                    onChange={(event) => {
                        setSelectedAuthType(event.target.key);
                        onChangeEndpointAuth(event.target.value, 'type');
                    }}
                    inputProps={{
                        name: 'key',
                        id: 'auth-type-select',
                    }}
                >
                    {authTypes.map((type) => (
                        <MenuItem value={type.key}>{type.value}</MenuItem>
                    ))}
                </TextField>
            </Grid>
            <Grid item xs={6} />

            {/** TODO - Add logic for when the OAuth grant type is set to password */}
            {(selectedAuthType === 'BASIC' || selectedAuthType === 'DIGEST') && (
                <Grid item xs={6}>
                    <TextField
                        disabled={isRestricted(['apim:api_create'], api)}
                        required
                        fullWidth
                        error={securityValidity && securityValidity.username === false}
                        helperText={
                            securityValidity && securityValidity.username === false ? (
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.no.username.error'
                                    defaultMessage='Username should not be empty'
                                />
                            ) : (
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.username.message'
                                    defaultMessage='Enter Username'
                                />
                            )
                        }
                        variant='outlined'
                        id='auth-userName'
                        label={(
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.user.name.input'
                                defaultMessage='Username'
                            />
                        )}
                        onChange={(event) => setEndpointSecurityInfo(
                            { ...endpointSecurityInfo, username: event.target.value },
                        )}
                        value={endpointSecurityInfo.username}
                        onBlur={() => validateAndUpdateSecurityInfo('username')}
                    />
                </Grid>
            )}

            {(selectedAuthType === 'BASIC' || selectedAuthType === 'DIGEST') && (
                <Grid item xs={6}>
                    <TextField
                        disabled={isRestricted(['apim:api_create'], api)}
                        required
                        fullWidth
                        error={securityValidity && securityValidity.password === false}
                        helperText={
                            securityValidity && securityValidity.password === false ? (
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.no.password.error'
                                    defaultMessage='Password should not be empty'
                                />
                            ) : (
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.password.message'
                                    defaultMessage='Enter Password'
                                />
                            )
                        }
                        variant='outlined'
                        type='password'
                        id='auth-password'
                        label={(
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.password.input'
                                defaultMessage='Password'
                            />
                        )}
                        value={endpointSecurityInfo.password}
                        onChange={(event) => setEndpointSecurityInfo(
                            { ...endpointSecurityInfo, password: event.target.value },
                        )}
                        onBlur={() => validateAndUpdateSecurityInfo('password')}
                    />
                </Grid>
            )}

            <Grid item xs={6}>
                <TextField
                    disabled={isRestricted(['apim:api_create'], api)}
                    required
                    fullWidth
                    error={securityValidity && securityValidity.tokenUrl === false}
                    helperText={
                        securityValidity && securityValidity.tokenUrl === false ? (
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.no.tokenUrl.error'
                                defaultMessage='Token URL should not be empty'
                            />
                        ) : (
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.tokenUrl.message'
                                defaultMessage='Enter Token URL'
                            />
                        )
                    }
                    variant='outlined'
                    id='auth-tokenUrl'
                    label={(
                        <FormattedMessage
                            id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.token.url.input'
                            defaultMessage='Token URL'
                        />
                    )}
                    onChange={(event) => setEndpointSecurityInfo(
                        { ...endpointSecurityInfo, tokenUrl: event.target.value },
                    )}
                    value={endpointSecurityInfo.tokenUrl}
                    onBlur={() => validateAndUpdateSecurityInfo('tokenUrl')}
                />
            </Grid>

            <Grid item xs={6}>
                <TextField
                    disabled={isRestricted(['apim:api_create'], api)}
                    required
                    fullWidth
                    error={securityValidity && securityValidity.apiKey === false}
                    helperText={
                        securityValidity && securityValidity.apiKey === false ? (
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.no.apiKey.error'
                                defaultMessage='API Key should not be empty'
                            />
                        ) : (
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.apiKey.message'
                                defaultMessage='Enter API Key'
                            />
                        )
                    }
                    variant='outlined'
                    id='auth-apiKey'
                    label={(
                        <FormattedMessage
                            id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.api.key.input'
                            defaultMessage='API Key'
                        />
                    )}
                    onChange={(event) => setEndpointSecurityInfo(
                        { ...endpointSecurityInfo, apiKey: event.target.value },
                    )}
                    value={endpointSecurityInfo.apiKey}
                    onBlur={() => validateAndUpdateSecurityInfo('apiKey')}
                />
            </Grid>

            <Grid item xs={6}>
                <TextField
                    disabled={isRestricted(['apim:api_create'], api)}
                    required
                    fullWidth
                    error={securityValidity && securityValidity.apiSecret === false}
                    helperText={
                        securityValidity && securityValidity.apiSecret === false ? (
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.no.apiSecret.error'
                                defaultMessage='API Secret should not be empty'
                            />
                        ) : (
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.apiSecret.message'
                                defaultMessage='Enter API Secret'
                            />
                        )
                    }
                    variant='outlined'
                    id='auth-apiSecret'
                    label={(
                        <FormattedMessage
                            id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.api.secret.input'
                            defaultMessage='API Secret'
                        />
                    )}
                    onChange={(event) => setEndpointSecurityInfo(
                        { ...endpointSecurityInfo, apiSecret: event.target.value },
                    )}
                    value={endpointSecurityInfo.apiSecret}
                    onBlur={() => validateAndUpdateSecurityInfo('apiSecret')}
                />
            </Grid>
        </Grid>
    );
}

EndpointSecurity.propTypes = {
    intl: PropTypes.func.isRequired,
    securityInfo: PropTypes.shape({}).isRequired,
    onChangeEndpointAuth: PropTypes.func.isRequired,
};

export default injectIntl(EndpointSecurity);
